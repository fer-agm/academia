package com.academia.mensajeria_service.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.academia.mensajeria_service.dto.*;
import com.academia.mensajeria_service.model.*;
import com.academia.mensajeria_service.repository.*;

@Service
public class MensajeService {

    private static final Logger log = LoggerFactory.getLogger(MensajeService.class); // Log exigido
    private final MensajeRepository repository;
    private final WebClient webClient;


    @Value("${api.usuario.exists}")
    private String usuarioExistsUrl;

  

    public MensajeService(MensajeRepository repository, WebClient webClient) {
        this.repository = repository;
        this.webClient = webClient;
    }

    public List<MensajeDTO> getAll() {
        log.info("Buscando todos los mensajes");
        return repository.findAll().stream().map(this::convertToDTO).toList();
    }

    public Optional<MensajeDTO> getById(Long id) {
        log.info("Buscando mensaje con ID: {}", id);
        return repository.findById(id).map(this::convertToDTO);
    }

    // public List<MensajeDTO> getByUsuario(String idUsuario) {
    //     log.info("Buscando mensajes con usuario: {}", idUsuario);
    //     return repository.findByIdEmisorOrIdReceptor(idUsuario, idUsuario).stream()
    //             .map(this::convertToDTO)
    //             .toList();
    // }


public List<MensajeDTO> listarPorEstudiante(String idEstudiante) {
    log.info("Buscando mensajes con usuario: {}", idEstudiante);
    List<Mensaje> comoEmisor = repository.findByIdEmisor(idEstudiante);
    List<Mensaje> comoReceptor = repository.findByIdReceptor(idEstudiante);

    return java.util.stream.Stream.concat(comoEmisor.stream(), comoReceptor.stream())
            .map(this::convertToDTO)
            .toList();
}


    public MensajeDTO generarMensaje(MensajeDTO dto, String authHeader) {
        log.info("Guardando registro de mensajes para emisor: {}", dto.getIdEmisor());


        verificarExiste(usuarioExistsUrl, dto.getIdEmisor(), authHeader,
                "El emisor con id " + dto.getIdEmisor() + " no existe");
        verificarExiste(usuarioExistsUrl, dto.getIdReceptor(), authHeader,
                "El receptor con id " + dto.getIdReceptor() + " no existe");
        Mensaje entidad = convertToEntity(dto);
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
        log.warn("Eliminando mensaje con ID: {}", id);
        repository.deleteById(id);
    }

    private MensajeDTO convertToDTO(Mensaje entity) {
        return new MensajeDTO(entity.getIdMensaje(), entity.getIdEmisor(), entity.getIdReceptor(), entity.getMensaje());
    }

    private Mensaje convertToEntity(MensajeDTO dto) {
        return new Mensaje(dto.getIdMensaje(), dto.getIdEmisor(), dto.getIdReceptor(), dto.getMensaje());
    }
}