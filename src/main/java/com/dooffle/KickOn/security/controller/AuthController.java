package com.dooffle.KickOn.security.controller;

import com.dooffle.KickOn.dto.UserDto;
import com.dooffle.KickOn.security.entity.RefreshToken;
import com.dooffle.KickOn.security.exception.TokenRefreshException;
import com.dooffle.KickOn.security.payload.TokenRefreshRequest;
import com.dooffle.KickOn.security.payload.TokenRefreshResponse;
import com.dooffle.KickOn.security.service.RefreshTokenService;
import com.dooffle.KickOn.services.UserService;
import com.dooffle.KickOn.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/refresh")
public class AuthController {


  @Autowired
  RefreshTokenService refreshTokenService;

  @Autowired
  private UserService userService;


  @PostMapping
  public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
    String requestRefreshToken = request.getRefreshToken();
    try{
      Optional<String> user =  refreshTokenService.findByToken(requestRefreshToken)
              .map(refreshTokenService::verifyExpiration)
              .map(RefreshToken::getUserId);
      UserDto userDto = userService.getUserDetailsByEmail(user.get());

      final String authorities = (String) userDto.getRoles().stream().map(x-> x.getName())
              .collect(Collectors.joining(","));

      String token = JwtUtils.generateTokenFromUsername(userDto.getUserId(), authorities);
      return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
    }catch (RuntimeException e){
      throw new TokenRefreshException(requestRefreshToken,
              "Refresh token is not in database!");
    }
  }

}