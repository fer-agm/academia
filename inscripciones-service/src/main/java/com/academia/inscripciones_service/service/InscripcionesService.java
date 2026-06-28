package com.academia.inscripciones_service.service;


import com.academia.inscripciones_service.model.Inscripciones;
import com.academia.inscripciones_service.repository.InscripcionesRepository;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import reactor.core.publisher.Mono;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
public class InscripcionesService {

    private final InscripcionesRepository inscripcionesRepository;
    private final WebClient webClient;

    @Value("${clases.service.url}")
    private String clasesServiceUrl;

    @Value("${api.curso.exists}")
    private String cursoExistsUrl;

    @Value("${api.usuario.exists}")
    private String usuarioExistsUrl;




    public InscripcionesService(InscripcionesRepository inscripcionesRepository, WebClient webClient) {
        this.inscripcionesRepository = inscripcionesRepository;
        this.webClient = webClient;
    }

public Mono<Map<String,Object>> crearInscripcionConValidaciones(Inscripciones inscripciones){
    log.info("Validando ID de curso: {}", inscripciones.getIdCurso());
    return webClient.get()  
        .uri(clasesServiceUrl + "/api/cursos/" + inscripciones.getIdCurso())
        .retrieve() 
        .bodyToMono(Void.class)  // ← Solo importa que exista, no los datos
        .then(Mono.defer(() -> {
            inscripciones.setFecha_inscripcion(LocalDateTime.now());
            if (inscripciones.getEstado() == null) {
                inscripciones.setEstado("ACTIVO");
            }
            Inscripciones saved = inscripcionesRepository.save(inscripciones);
            Map<String, Object> response = new HashMap<>();
            response.put("inscripcion", saved);
            response.put("mensaje", "Inscripción creada exitosamente");
            return Mono.just(response);
        }))
        .onErrorResume(e -> {
            log.error("Error al validar curso: {}", e.getMessage());
            return Mono.error(new RuntimeException("Curso con ID " + inscripciones.getIdCurso() + " no encontrado"));
        });
}


    public List<Inscripciones> listarTodas() {
        log.info("[InscripcionesService] Obteniendo todas las inscripciones");
        return inscripcionesRepository.findAll();
    }

    public Inscripciones buscarPorId(Long id) {
        log.info("[InscripcionesService] Buscando inscripción con ID: {}", id);
        Inscripciones ins = inscripcionesRepository.findById(id).orElse(null);
        if (ins == null) {
            log.warn("[InscripcionesService] Inscripción con ID {} no encontrada", id);
        }
        return ins;
    }

    public Inscripciones crearInscripcion(Inscripciones inscripciones, String authHeader) {
        log.info("[InscripcionesService] Creando inscripción para estudiante: {}", inscripciones.getIdEstudiante());
        validarCurso(inscripciones.getIdCurso(), authHeader);
        validarEstudiante(inscripciones.getIdEstudiante(), authHeader);
        inscripciones.setFecha_inscripcion(LocalDateTime.now());
        if (inscripciones.getEstado() == null) {
            inscripciones.setEstado("ACTIVO");
        }
        Inscripciones saved = inscripcionesRepository.save(inscripciones);
        log.info("[InscripcionesService] Inscripción creada con ID: {}", saved.getId_inscripcion());
        return saved;
    }

    private void validarCurso(Long idCurso, String authHeader) {
        log.info("[InscripcionesService] Validando existencia del curso con id: {}", idCurso);
        Boolean existe = webClient.get()
                .uri(String.format(cursoExistsUrl, idCurso))
                .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if (!Boolean.TRUE.equals(existe)) {
            log.warn("[InscripcionesService] El curso con id {} no existe", idCurso);
            throw new IllegalArgumentException("El curso con id " + idCurso + " no existe");
        }
    }

    private void validarEstudiante(String run, String authHeader) {
        log.info("[InscripcionesService] Validando existencia del estudiante con RUN: {}", run);
        Boolean existe = webClient.get()
                .uri(String.format(usuarioExistsUrl, run))
                .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if (!Boolean.TRUE.equals(existe)) {
            log.warn("[InscripcionesService] El estudiante con RUN {} no existe", run);
            throw new IllegalArgumentException("El estudiante con RUN " + run + " no existe");
        }
    }

    public List<Inscripciones> listarPorEstudiante(String run) {
        log.info("[InscripcionesService] Buscando inscripciones del estudiante: {}", run);
        return inscripcionesRepository.findByIdEstudiante(run);
    }

    public Inscripciones actualizar(Long id, Inscripciones datos) {
        log.info("[InscripcionesService] Actualizando inscripción con ID: {}", id);
        return inscripcionesRepository.findById(id).map(ins -> {
            ins.setEstado(datos.getEstado());
            ins.setIdCurso(datos.getIdCurso());
            Inscripciones updated = inscripcionesRepository.save(ins);
            log.info("[InscripcionesService] Inscripción con ID {} actualizada", id);
            return updated;
        }).orElse(null);
    }

    public boolean eliminar(Long id) {
        log.info("[InscripcionesService] Eliminando inscripción con ID: {}", id);
        if (inscripcionesRepository.existsById(id)) {
            inscripcionesRepository.deleteById(id);
            log.info("[InscripcionesService] Inscripción con ID {} eliminada", id);
            return true;
        }
        log.warn("[InscripcionesService] Inscripción con ID {} no encontrada para eliminar", id);
        return false;
    }
}