package com.example.api_gateway.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
public class RouterConfig {

    private final WebClient webClient;

    public RouterConfig() {
        HttpClient httpClient = HttpClient.create().followRedirect(false);
        this.webClient = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
    }

    @Bean
    public RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route()
            .path("/api/auth", b -> b.route(path("/**"), req -> proxy(req, "http://auth-service:8087")))
            .path("/api/usuarios", b -> b.route(path("/**"), req -> proxy(req, "http://user-service:8086")))
            .path("/api/roles", b -> b.route(path("/**"), req -> proxy(req, "http://user-service:8086")))
            .path("/api/pagos", b -> b.route(path("/**"), req -> proxy(req, "http://pago-service:8082")))
            .path("/api/transacciones", b -> b.route(path("/**"), req -> proxy(req, "http://pago-service:8082")))
            .path("/api/cursos", b -> b.route(path("/**"), req -> proxy(req, "http://clases-service:8083")))
            .path("/api/categorias", b -> b.route(path("/**"), req -> proxy(req, "http://clases-service:8083")))
            .path("/api/clases", b -> b.route(path("/**"), req -> proxy(req, "http://clases-service:8083")))
            .path("/api/examenes", b -> b.route(path("/**"), req -> proxy(req, "http://evaluaciones-service:8084")))
            .path("/api/preguntas", b -> b.route(path("/**"), req -> proxy(req, "http://evaluaciones-service:8084")))
            .path("/api/alternativas", b -> b.route(path("/**"), req -> proxy(req, "http://evaluaciones-service:8084")))
            .path("/api/inscripciones", b -> b.route(path("/**"), req -> proxy(req, "http://inscripciones-service:8085")))
            .path("/api/cupos", b -> b.route(path("/**"), req -> proxy(req, "http://inscripciones-service:8085")))
            .path("/api/calificaciones", b -> b.route(path("/**"), req -> proxy(req, "http://calificaciones-service:8089")))
            .path("/api/certificados", b -> b.route(path("/**"), req -> proxy(req, "http://certificado-service:8088")))
            .build();
    }

    private Mono<ServerResponse> proxy(ServerRequest request, String targetBase) {
        URI targetUri = URI.create(targetBase + request.uri().getRawPath() +
            (request.uri().getRawQuery() != null ? "?" + request.uri().getRawQuery() : ""));

        return webClient
            .method(request.method())
            .uri(targetUri)
            .headers(headers -> headers.addAll(request.headers().asHttpHeaders()))
            .body(request.bodyToMono(byte[].class), byte[].class)
            .exchangeToMono(clientResponse ->
                ServerResponse.status(clientResponse.statusCode())
                    .headers(h -> h.addAll(clientResponse.headers().asHttpHeaders()))
                    .body(clientResponse.bodyToMono(byte[].class), byte[].class)
            );
    }
}