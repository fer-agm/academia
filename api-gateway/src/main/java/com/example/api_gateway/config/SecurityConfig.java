package com.example.api_gateway.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}")
    // private String secret;

    @Bean
 public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .authorizeExchange(auth -> auth
                .pathMatchers("/api/auth/**").permitAll()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/swagger-ui/**").permitAll()
                .pathMatchers("/v3/api-docs/**").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                .pathMatchers(org.springframework.http.HttpMethod.POST, "/api/usuarios/crear").permitAll()
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }

    @Bean
    public JwtAuthFilter jwtFilter(){
        return new JwtAuthFilter();
    }


    // @Bean
    // public ReactiveJwtDecoder jwtDecoder() {
    //     SecretKeySpec key = new SecretKeySpec(
    //         secret.getBytes(StandardCharsets.UTF_8),
    //         "HmacSHA256"
    //     );
    //     return NimbusReactiveJwtDecoder.withSecretKey(key).build();
    // }
}