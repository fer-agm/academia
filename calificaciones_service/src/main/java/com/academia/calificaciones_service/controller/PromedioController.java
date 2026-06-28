package com.academia.calificaciones_service.controller;

import com.academia.calificaciones_service.dto.PromedioDTO;
import com.academia.calificaciones_service.service.PromedioService;
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
@RequestMapping("/api/promedios")
@Tag(name = "Promedios", description = "Operaciones CRUD sobre los promedios generales de los estudiantes")
public class PromedioController {

    private static final Logger log = LoggerFactory.getLogger(PromedioController.class);
    private final PromedioService promedioService;

    public PromedioController(PromedioService promedioService) {
        this.promedioService = promedioService;
    }

    @Operation(summary = "Listar todos los promedios",
            description = "Devuelve la lista completa de promedios registrados, cada uno con su enlace HATEOAS autorreferencial.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de promedios obtenido correctamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<PromedioDTO>> getAll() {
        log.info("Petición entrante: Listar todos los promedios");
        List<PromedioDTO> dtos = promedioService.getAll();
        
        // HATEOAS enlace autorreferencial en colecciones GET 
        dtos.forEach(dto -> dto.add(linkTo(methodOn(PromedioController.class).getById(dto.getIdPromedio())).withSelfRel()));
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener un promedio por ID",
            description = "Busca y devuelve un promedio específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promedio encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un promedio con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PromedioDTO> getById(
            @Parameter(description = "Identificador único del promedio", example = "1") @PathVariable Long id) {
        log.info("Petición entrante: Buscar promedio ID {}", id);
        return promedioService.getById(id)
                .map(dto -> {
                    dto.add(linkTo(methodOn(PromedioController.class).getAll()).withRel("lista-completa"));
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un promedio",
            description = "Registra un nuevo promedio a partir de los datos enviados en el cuerpo de la petición.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promedio creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<PromedioDTO> crear(@Valid @RequestBody PromedioDTO promedioDTO,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        log.info("Petición entrante: Crear promedio");
        return ResponseEntity.ok(promedioService.guardar(promedioDTO, authHeader));
    }

    @Operation(summary = "Actualizar un promedio",
            description = "Actualiza los datos de un promedio existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promedio actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "No existe un promedio con el ID indicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PromedioDTO> actualizar(
            @Parameter(description = "Identificador único del promedio a actualizar", example = "1") @PathVariable Long id,
            @Valid @RequestBody PromedioDTO promedioDTO,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        log.info("Petición entrante: Actualizar promedio ID {}", id);
        return promedioService.getById(id)
                .map(existing -> {
                    promedioDTO.setIdPromedio(id);
                    return ResponseEntity.ok(promedioService.guardar(promedioDTO, authHeader));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un promedio",
            description = "Elimina un promedio existente identificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Promedio eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe un promedio con el ID indicado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador único del promedio a eliminar", example = "1") @PathVariable Long id) {
        log.info("Petición entrante: Eliminar promedio ID {}", id);
        return promedioService.getById(id)
                .map(existing -> {
                    promedioService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}