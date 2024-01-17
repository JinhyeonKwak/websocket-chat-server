package com.websocket.chat.service;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    private final String SECRET_KEY = UUID.randomUUID().toString();
    private final long ACCESS_TOKEN_EXPIRED = 1000L * 60 * 60; // 1시간

    public String generateAccessToken(String username) {
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuer("ADMIN")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes())
                .compact();
    }

    public String getUsername(String token) {
        return Jwts
                .parser()
                .setSigningKey(SECRET_KEY.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        return this.getClaims(token) != null;
    }

    private Jws<Claims> getClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .parseClaimsJws(token);
        } catch (SignatureException e) {
            log.error("Invalid JWT signature");
            throw e;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token");
            throw e;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token");
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty");
            throw e;
        }
    }
}
