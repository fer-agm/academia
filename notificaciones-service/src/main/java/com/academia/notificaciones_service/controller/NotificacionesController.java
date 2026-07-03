package com.academia.notificaciones_service.controller;

import com.academia.notificaciones_service.dto.NotificacionesDTO;
import com.academia.notificaciones_service.service.NotificacionesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/notificaciones")
@Tag(name = "Notificaciones", description = "Operaciones CRUD sobre las notificaciones")
public class NotificacionesController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionesController.class);
    private final NotificacionesService notificacionesService;

    public NotificacionesController(NotificacionesService notificacionesService) {
        this.notificacionesService = notificacionesService;
    }

    @Operation(summary = "Listar todos las notificaciones",
            description = "Devuelve la lista completa de notificaciones, cada uno con su enlace HATEOAS autorreferencial.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de notificaciones obtenido correctamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<NotificacionesDTO>> getAll() {
        log.info("Petición entrante: Listar todas las notificaciones");
        List<NotificacionesDTO> dtos = notificacionesService.getAll();
        
        // HATEOAS enlace autorreferencial en colecciones GET 
        dtos.forEach(dto -> dto.add(linkTo(methodOn(NotificacionesController.class).getById(dto.getIdNotificacion())).withSelfRel()));
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener una notificación por ID",
            description = "Busca y devuelve una notificación específica según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una notificación con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificacionesDTO> getById(
            @Parameter(description = "Identificador único de la notificación", example = "1") @PathVariable Long id) {
        log.info("Petición entrante: Buscar notificación ID {}", id);
        return notificacionesService.getById(id)
                .map(dto -> {
                    dto.add(linkTo(methodOn(NotificacionesController.class).getAll()).withRel("lista-completa"));
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una notificación",
            description = "Registra una nueva notificación a partir de los datos enviados en el cuerpo de la petición.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<NotificacionesDTO> crear(@Valid @RequestBody NotificacionesDTO notificacionesDTO,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        log.info("Petición entrante: Crear notificación");
        return ResponseEntity.ok(notificacionesService.guardar(notificacionesDTO, authHeader));
    }

    @Operation(summary = "Actualizar una notificación",
            description = "Actualiza los datos de una notificación existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "No existe una notificación con el ID indicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<NotificacionesDTO> actualizar(
            @Parameter(description = "Identificador único de la notificación a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody NotificacionesDTO notificacionesDTO,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        log.info("Petición entrante: Actualizar notificación ID {}", id);
        return notificacionesService.getById(id)
                .map(existing -> {
                    notificacionesDTO.setIdNotificacion(id);
                    return ResponseEntity.ok(notificacionesService.guardar(notificacionesDTO, authHeader));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar una notificación",
            description = "Elimina una notificación existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificación eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe una notificación con el ID indicado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador único de la notificación a eliminar", example = "1") @PathVariable Long id) {
        log.info("Petición entrante: Eliminar notificación ID {}", id);
        return notificacionesService.getById(id)
                .map(existing -> {
                    notificacionesService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}