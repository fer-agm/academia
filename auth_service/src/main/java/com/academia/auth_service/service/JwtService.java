package com.academia.auth_service.service;

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

    @Value("${jwt.secret}")
    private String secret;

    private Key getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // private final Key key = Keys.hmacShaKeyFor("esta_es_mi_compleja_clave_secreta_para_jwt_2026_academia_noafer".getBytes());
//    private final Key key = Keys.hmacShaKeyFor("9f3c2a87d1e64b55c6f4ab90e1d73c5f2d89b7aa34df56e0a1bf92c47e68d12f".getBytes());

    public String generateToken (String run) {
        Date ahora = new Date();
        Date expiration = new Date(ahora.getTime() + 1000L*60*60*8); // 8 horas
        return Jwts.builder()
        .setSubject(run)
        .setIssuedAt(ahora)
        .setExpiration(expiration)
        .signWith(getKey(), SignatureAlgorithm.HS256)
        .compact();
    }

    public String getRunFromToken(String token) {
        if (token == null || token.isBlank()) return null;
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        try {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
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
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(jwt);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }




}
