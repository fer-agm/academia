package com.example.api_gateway.config;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

public class JwtAuthFilter implements WebFilter {

    @Value("${jwt.secret}")
    private String secret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // Rutas públicas — dejar pasar sin token (login y creación de usuario)
        if (path.startsWith("/api/auth/") ||
            path.equals("/api/usuarios/crear") ||
            path.startsWith("/swagger-ui") ||
            path.contains("/v3/api-docs") ||
            path.startsWith("/webjars/")
        ) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Falta el token. Agrega el header 'Authorization: Bearer <token>'.");
        }

        String token = authHeader.substring(7).trim();
        try {
            Key key = Keys.hmacShaKeyFor(secret.getBytes());
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

            UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(claims.getSubject(), null, Collections.emptyList());

            return chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

        } catch (ExpiredJwtException e) {
            return unauthorized(exchange, "Token expirado. Inicia sesión nuevamente.");
        } catch (JwtException | IllegalArgumentException e) {
            // firma incorrecta, token mal formado, vacío, etc.
            return unauthorized(exchange, "Token no válido.");
        }
    }

    /** Responde 401 con un cuerpo JSON claro en vez de un 401 vacío. */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String mensaje) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"status\":401,\"error\":\"" + mensaje + "\"}";
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
