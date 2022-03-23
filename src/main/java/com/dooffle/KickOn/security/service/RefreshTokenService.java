package com.dooffle.KickOn.security.service;


import com.dooffle.KickOn.repository.UserRepository;
import com.dooffle.KickOn.security.entity.RefreshToken;
import com.dooffle.KickOn.security.exception.TokenRefreshException;
import com.dooffle.KickOn.security.repo.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;


@Service
public class RefreshTokenService {
  @Value("${token.refreshTokenDurationDay}")
  private Long refreshTokenDurationDay;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserRepository userRepository;

  public Optional<RefreshToken> findByToken(String token) {
    return refreshTokenRepository.findByToken(token);
  }

  public RefreshToken createRefreshToken(String userId) {
    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUserId(userId);
    refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationDay* 86400000));
    refreshToken.setToken(UUID.randomUUID().toString());
    deleteByUserId(userId);

    refreshToken = refreshTokenRepository.save(refreshToken);
    return refreshToken;
  }

  public RefreshToken verifyExpiration(RefreshToken token) {
    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
      refreshTokenRepository.delete(token);
      throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
    }

    return token;
  }

  public void deleteByUserId(String userId) {
    RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId);
    if(refreshToken!=null){
      refreshTokenRepository.delete(refreshToken);
    }

  }
}