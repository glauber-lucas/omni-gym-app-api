package com.example.omnigym.core.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class TokenService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    public TokenService(@Value("${jwt.secret:change-me-please}") String secret,
                        @Value("${jwt.expiration-ms:3600000}") long expirationMs) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
        this.expirationMs = expirationMs;
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs * 24 * 7); // 7 dias

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("type", "refresh")
                .withIssuedAt(now)
                .withExpiresAt(exp)
                .sign(algorithm);
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT decoded = verifier.verify(token);
            // Tokens de acesso não devem possuir a claim type="refresh"
            return decoded.getClaim("type").isMissing() || !"refresh".equals(decoded.getClaim("type").asString());
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            DecodedJWT decoded = verifier.verify(token);
            // Tokens de refresh devem ter a claim type="refresh"
            return "refresh".equals(decoded.getClaim("type").asString());
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        DecodedJWT decoded = verifier.verify(token);
        return decoded.getSubject();
    }

    public long getExpirationMs() {
        return expirationMs;
    }
}

