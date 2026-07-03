package com.academia.notificaciones_service.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.academia.notificaciones_service.dto.*;
import com.academia.notificaciones_service.model.*;
import com.academia.notificaciones_service.repository.*;

@Service
public class NotificacionesService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesService.class); // Log exigido
    private final NotificacionesRepository repository;
    private final WebClient webClient;


    @Value("${api.usuario.exists}")
    private String usuarioExistsUrl;


    @Value("${api.certificado.exists}")
    private String certificadoExistsUrl;       

    public NotificacionesService(NotificacionesRepository repository, WebClient webClient) {
        this.repository = repository;
        this.webClient = webClient;
    }

    public List<NotificacionesDTO> getAll() {
        log.info("Buscando todas las notificaciones");
        return repository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Optional<NotificacionesDTO> getById(Long id) {
        log.info("Buscando notificacion con ID: {}", id);
        return repository.findById(id).map(this::convertToDTO);
    }

    public NotificacionesDTO guardar(NotificacionesDTO dto, String authHeader) {
        log.info("Guardando registro de notificaciones para estudiante: {}", dto.getIdEstudiante());


        verificarExiste(certificadoExistsUrl, dto.getIdCertificado(), authHeader,
                "El certificado con id " + dto.getIdCertificado() + " no existe");
        verificarExiste(usuarioExistsUrl, dto.getIdEstudiante(), authHeader,
                "El estudiante con RUN " + dto.getIdEstudiante() + " no existe");
        Notificaciones entidad = convertToEntity(dto);
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
        log.warn("Eliminando notificación con ID: {}", id);
        repository.deleteById(id);
    }

    private NotificacionesDTO convertToDTO(Notificaciones entity) {
        return new NotificacionesDTO(entity.getIdNotificacion(), entity.getIdEstudiante(), entity.getIdCertificado());
    }

    private Notificaciones convertToEntity(NotificacionesDTO dto) {
        return new Notificaciones(dto.getIdNotificacion(), dto.getIdEstudiante(), dto.getIdCertificado());
    }
}