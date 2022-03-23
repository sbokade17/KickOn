package com.dooffle.KickOn.controllers;

import com.dooffle.KickOn.models.GetUserDetailsResponseModel;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@RestController
@RequestMapping("/auth")
public class OAuth2Controller {

    @Autowired
    NetHttpTransport transport;

    @Autowired
    JsonFactory jsonFactory;


    private static final String CLIENT_ID = "5342005664-68lgdmfl8vq502h7iho9evb3o79ap5ar.apps.googleusercontent.com";

    @PostMapping("/google/{token}")
    public ResponseEntity<GetUserDetailsResponseModel> googleSignIn(@PathVariable("token") String token) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        // (Receive idTokenString by HTTPS POST)

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            System.out.println(email);
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            System.out.println(emailVerified);
            String name = (String) payload.get("name");
            System.out.println(name);
            String pictureUrl = (String) payload.get("picture");
            System.out.println(pictureUrl);
            String locale = (String) payload.get("locale");
            System.out.println(locale);
            String familyName = (String) payload.get("family_name");
            System.out.println(familyName);
            String givenName = (String) payload.get("given_name");
            System.out.println(givenName);

            // Use or store profile information
            // ...

        } else {
            System.out.println("Invalid ID token.");
        }

        GetUserDetailsResponseModel response= new GetUserDetailsResponseModel();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
