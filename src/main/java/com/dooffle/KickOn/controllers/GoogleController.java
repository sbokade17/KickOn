package com.dooffle.KickOn.controllers;

import com.dooffle.KickOn.dto.RoleDto;
import com.dooffle.KickOn.dto.UserDto;
import com.dooffle.KickOn.exception.CustomAppException;
import com.dooffle.KickOn.models.GetUserDetailsResponseModel;
import com.dooffle.KickOn.models.GoogleValidateModel;
import com.dooffle.KickOn.services.UserService;
import com.dooffle.KickOn.utils.Constants;
import com.dooffle.KickOn.utils.JwtUtils;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/google")
public class GoogleController {

    @Value("${app.google.clientId}")
    private String clientId;
    @Value("${app.google.clientSecret}")
    private String clientSecret;

    @Autowired
    HttpTransport transport;
    @Autowired
    JsonFactory jsonFactory;

    @Autowired
    UserService userService;

    @PostMapping("/validate")
    public ResponseEntity<GetUserDetailsResponseModel> validateUser(@RequestBody GoogleValidateModel googleValidateModel) throws IOException {
        try{

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                    // Specify the CLIENT_ID of the app that accesses the backend:
                    .setAudience(Collections.singletonList(clientId))
                    // Or, if multiple clients access the backend:
                    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                    .build();

            // (Receive idTokenString by HTTPS POST)

            GoogleTokenResponse tokenResponse =
                    new GoogleAuthorizationCodeTokenRequest(
                            new NetHttpTransport(),
                            JacksonFactory.getDefaultInstance(),
                            "https://oauth2.googleapis.com/token",
                            clientId,
                            clientSecret,
                            googleValidateModel.getAccessToken(),
                            "")  // Specify the same redirect URI that you use with your web
                            // app. If you don't have a web version of your app, you can
                            // specify an empty string.
                            .execute();
            GoogleIdToken idToken = tokenResponse.parseIdToken();
       //     GoogleIdToken idToken = verifier.verify(googleValidateModel.getAccessToken());

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                // Print user identifier
                String userId = payload.getSubject();
//                System.out.println("User ID: " + userId);

                // Get profile information from payload
                String email = payload.getEmail();
//                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//                String name = (String) payload.get("name");
//                String pictureUrl = (String) payload.get("picture");
//                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");

                // Use or store profile information
                // ...
                UserDto userDetails = userService.getUserDetailsByEmailWithoutException(email);
                if(userDetails==null){
                    UserDto user = new UserDto();
                    user.setEmail(email);
                    user.setFirstName(givenName);
                    user.setLastName(familyName);
                    user.setPassword(userId);
                    user.setUserType(Constants.USER);
                    userService.createUser(user);
                }
                return ResponseEntity.status(HttpStatus.OK).body(generateJwtToken(email));
            } else {
                throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, "Could not validate access code");
            }

        }catch (RuntimeException e){
            throw new CustomAppException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
        }

    }

    private GetUserDetailsResponseModel generateJwtToken(String email) {

        UserDto userDto = userService.getUserDetailsByEmail(email);

        final String authorities = userDto.getRoles().stream().map(x-> x.getName())
                .collect(Collectors.joining(","));

        String token= JwtUtils.generateJwtToken(userDto.getUserId(), authorities);

        GetUserDetailsResponseModel userResponse = ObjectMapperUtils.map(userDto, GetUserDetailsResponseModel.class);
        if(userResponse.getRoles().size()==1){
            userResponse.setUserType(userResponse.getRoles().stream().findFirst().map(x->x.getName()).get());
        }
        userResponse.setToken(token);
        return userResponse;
    }

}
