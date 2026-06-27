package com.academia.auth_service.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Service
public class JwtService {

    // Same secret the gateway and every resource server validate with (JWT_SECRET env var).
    private final Key key;

    public JwtService(@Value("${JWT_SECRET:esta_es_mi_compleja_clave_secreta_para_jwt_2026_academia_noafer}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken (String run) {
        Date ahora = new Date();
        Date expiration = new Date(ahora.getTime() + 1000*60*60);
        return Jwts.builder()
        .setSubject(run)
        .setIssuedAt(ahora)
        .setExpiration(expiration)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
    }

    public String getRunFromToken(String token) {
        if (token == null || token.isBlank()) return null;
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(jwt)
            .getBody()
            .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }    


    public boolean isValid(String token) {
        if (token == null || token.isBlank()) return false;
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }




}
