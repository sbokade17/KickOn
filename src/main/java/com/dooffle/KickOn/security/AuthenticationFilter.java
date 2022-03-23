package com.dooffle.KickOn.security;

import com.dooffle.KickOn.dto.UserDto;
import com.dooffle.KickOn.exception.ErrorResponse;
import com.dooffle.KickOn.models.GetUserDetailsResponseModel;
import com.dooffle.KickOn.models.LoginRequestModel;
import com.dooffle.KickOn.security.entity.RefreshToken;
import com.dooffle.KickOn.security.service.RefreshTokenService;
import com.dooffle.KickOn.services.UserService;
import com.dooffle.KickOn.utils.JwtUtils;
import com.dooffle.KickOn.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private UserService userService;
    private Environment environment;
    private RefreshTokenService refreshTokenService;



    public AuthenticationFilter(UserService userService, Environment environment, RefreshTokenService refreshTokenService){
        this.userService = userService;
        this.environment = environment;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try{
            LoginRequestModel requestModel=new ObjectMapper().readValue(request.getInputStream(), LoginRequestModel.class);


            final Authentication authenticate = getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                    requestModel.getEmail(),
                    requestModel.getPassword(),
                    new ArrayList<>()
            ));

            return authenticate;
        }catch (IOException e){
            System.out.println("throwing exception");
            throw new RuntimeException();
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        String userName= ((User)authResult.getPrincipal()).getUsername();
        UserDto userDto = userService.getUserDetailsByEmail(userName);

        final String authorities = (String) userDto.getRoles().stream().map(x-> x.getName())
                .collect(Collectors.joining(","));

        String token= JwtUtils.generateJwtToken(userDto.getUserId(), authorities);

        Gson gson = new Gson();
        GetUserDetailsResponseModel userResponse = ObjectMapperUtils.map(userDto, GetUserDetailsResponseModel.class);
        if(userResponse.getRoles().size()==1){
            userResponse.setUserType(userResponse.getRoles().stream().findFirst().map(x->x.getName()).get());
        }
        userResponse.setToken(token);
        String userDtoString = gson.toJson(userResponse);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userName);
        response.addHeader("Access-Control-Expose-Headers","token, userId, refreshToken");
        response.addHeader("token", token);
        response.addHeader("userId", userDto.getUserId());
        response.addHeader("refreshToken",refreshToken.getToken());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(userDtoString);
        response.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        Gson gson = new Gson();
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setError("Username/Password is incorrect!");
        errorResponse.setMessage("Username/Password is incorrect!");
        errorResponse.setStatus(403);
        errorResponse.setTimestamp(new Date());
        String userDtoString = gson.toJson(ObjectMapperUtils.map(errorResponse, ErrorResponse.class));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(403);
        response.getWriter().write(userDtoString);
        response.getWriter().flush();
        //super.unsuccessfulAuthentication(request, response, failed);
    }
}
