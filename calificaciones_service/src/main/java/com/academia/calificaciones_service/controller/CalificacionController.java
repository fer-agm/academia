package com.academia.calificaciones_service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.calificaciones_service.dto.CalificacionDTO;
import com.academia.calificaciones_service.service.CalificacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/calificaciones")
@Tag(name = "Calificaciones", description = "Operaciones CRUD sobre las calificaciones de los estudiantes")
public class CalificacionController {

    private static final Logger log = LoggerFactory.getLogger(CalificacionController.class); // Trazabilidad 
    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @Operation(summary = "Listar todas las calificaciones",
            description = "Devuelve la lista completa de calificaciones registradas, cada una con su enlace HATEOAS autorreferencial.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de calificaciones obtenido correctamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<CalificacionDTO>> getAll() {
        log.info("Petición entrante: Listar todas las calificaciones");
        List<CalificacionDTO> dtos = calificacionService.getAll();
        
        // Aplicar HATEOAS obligatoriamente solo a métodos GET [cite: 29, 30]
        dtos.forEach(dto -> dto.add(linkTo(methodOn(CalificacionController.class).getById(dto.getIdCalificacion())).withSelfRel()));
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener una calificación por ID",
            description = "Busca y devuelve una calificación específica según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calificación encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe una calificación con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CalificacionDTO> getById(
            @Parameter(description = "Identificador único de la calificación", example = "1") @PathVariable Long id) {
        log.info("Petición entrante: Buscar calificación ID {}", id);
        return calificacionService.getById(id)
                .map(dto -> {
                    dto.add(linkTo(methodOn(CalificacionController.class).getAll()).withRel("lista-completa"));
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear una calificación",
            description = "Registra una nueva calificación a partir de los datos enviados en el cuerpo de la petición.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calificación creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<CalificacionDTO> crear(@Valid @RequestBody CalificacionDTO calificacionDTO,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        log.info("Petición entrante: Crear calificación");
        return ResponseEntity.ok(calificacionService.guardar(calificacionDTO, authHeader));
    }

    @Operation(summary = "Actualizar una calificación",
            description = "Actualiza los datos de una calificación existente identificada por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Calificación actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "No existe una calificación con el ID indicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<CalificacionDTO> actualizar(
            @Parameter(description = "Identificador único de la calificación a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody CalificacionDTO calificacionDTO,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        log.info("Petición entrante: Actualizar calificación ID {}", id);
        return calificacionService.getById(id)
                .map(existing -> {
                    calificacionDTO.setIdCalificacion(id);
                    return ResponseEntity.ok(calificacionService.guardar(calificacionDTO, authHeader));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar una calificación",
            description = "Elimina una calificación existente identificada por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Calificación eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe una calificación con el ID indicado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador único de la calificación a eliminar", example = "1") @PathVariable Long id) {
        log.info("Petición entrante: Eliminar calificación ID {}", id);
        return calificacionService.getById(id)
                .map(existing -> {
                    calificacionService.borrar(id);
                    return ResponseEntity.noContent().<Void>build(); 
                })
                .orElse(ResponseEntity.notFound().build());
    }
}