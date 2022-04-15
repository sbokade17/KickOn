package com.dooffle.KickOn.utils;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public  class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${token.secret}")
    private String jwtSecret;

    private static String secret;

    @Value("${token.expiration.hours}")
    private int jwtExpirationHrs;

    private static int expiry;

    @Value("${token.secret}")
    public void setSecretStatic(String jwtSecret){
        JwtUtils.secret = jwtSecret;
    }

    @Value("${token.expiration.hours}")
    public void setExpiryStatic(int jwtExpirationHrs){
        JwtUtils.expiry = jwtExpirationHrs;
    }


    public static String generateJwtToken(String userId, String authorities) {
        return generateTokenFromUsername(userId, authorities);
    }

    public static String generateTokenFromUsername(String username, String authorities) {
        return Jwts.builder().setSubject(username)
                .claim("AUTHORITIES", authorities)
                .setExpiration(new Date(System.currentTimeMillis()+ expiry*3600000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }



    public static boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }

}
