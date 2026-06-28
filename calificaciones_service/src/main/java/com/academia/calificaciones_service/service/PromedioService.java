package com.academia.calificaciones_service.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.academia.calificaciones_service.dto.PromedioDTO;
import com.academia.calificaciones_service.model.Promedio;
import com.academia.calificaciones_service.repository.PromedioRepository;

@Service
public class PromedioService {

    private static final Logger log = LoggerFactory.getLogger(PromedioService.class); // Log exigido
    private final PromedioRepository repository;
    private final WebClient webClient;

    @Value("${api.curso.exists}")
    private String cursoExistsUrl;

    @Value("${api.usuario.exists}")
    private String usuarioExistsUrl;

    public PromedioService(PromedioRepository repository, WebClient webClient) {
        this.repository = repository;
        this.webClient = webClient;
    }

    public List<PromedioDTO> getAll() {
        log.info("Buscando todos los promedios generales");
        return repository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Optional<PromedioDTO> getById(Long id) {
        log.info("Buscando promedio con ID: {}", id);
        return repository.findById(id).map(this::convertToDTO);
    }

    public PromedioDTO guardar(PromedioDTO dto, String authHeader) {
        log.info("Guardando registro de promedio para estudiante: {}", dto.getIdEstudiante());
        verificarExiste(cursoExistsUrl, dto.getIdCurso(), authHeader,
                "El curso con id " + dto.getIdCurso() + " no existe");
        verificarExiste(usuarioExistsUrl, dto.getIdEstudiante(), authHeader,
                "El estudiante con RUN " + dto.getIdEstudiante() + " no existe");
        Promedio entidad = convertToEntity(dto);
        return convertToDTO(repository.save(entidad));
    }

    private void verificarExiste(String url, Object id, String authHeader, String mensajeNoExiste) {
        Boolean existe = webClient.get().uri(String.format(url, id))
                .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                .retrieve().bodyToMono(Boolean.class).block();
        if (existe == null) throw new IllegalStateException("No se pudo validar: " + mensajeNoExiste);
        if (Boolean.FALSE.equals(existe)) throw new IllegalArgumentException(mensajeNoExiste);
    }

    public void borrar(Long id) {
        log.warn("Eliminando promedio con ID: {}", id);
        repository.deleteById(id);
    }

    private PromedioDTO convertToDTO(Promedio entity) {
        return new PromedioDTO(entity.getIdPromedio(), entity.getIdCurso(), entity.getIdEstudiante(), entity.getPromedioGeneral(), entity.getTotalEvaluaciones());
    }

    private Promedio convertToEntity(PromedioDTO dto) {
        return new Promedio(dto.getIdPromedio(), dto.getIdEstudiante(), dto.getIdCurso(), dto.getPromedioGeneral(), dto.getTotalEvaluaciones());
    }
}