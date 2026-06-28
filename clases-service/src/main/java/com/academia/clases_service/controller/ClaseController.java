package com.academia.clases_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.clases_service.dto.ClaseDTO;
import com.academia.clases_service.model.Clase;
import com.academia.clases_service.service.ClaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clases")
@Tag(name = "Clases", description = "Operaciones para la gestión de clases")
public class ClaseController {

    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }

    @GetMapping ("/listar")
    @Operation(summary = "Listar clases", description = "Obtiene la lista de todas las clases registradas con sus enlaces HATEOAS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de clases obtenido correctamente")
    })
    public ResponseEntity<CollectionModel<ClaseDTO>> getAll() {
        List<ClaseDTO> clasesDto = claseService.getAll().stream()
        .map(ClaseDTO::fromModel)
        .map(this::agregarLinks)
        .collect(Collectors.toList());

        CollectionModel<ClaseDTO> collectionModel = CollectionModel.of(clasesDto);
        collectionModel.add(linkTo(methodOn(ClaseController.class).getAll()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener clase por ID", description = "Busca y devuelve una clase a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase encontrada"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<ClaseDTO> getById(
            @Parameter(description = "Identificador de la clase") @PathVariable Long id) {
        return claseService.getById(id)
                .map(ClaseDTO::fromModel)
                .map(this::agregarLinks)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/curso/{idCurso}")
    @Operation(summary = "Listar clases por curso", description = "Obtiene las clases que pertenecen a un curso determinado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de clases del curso obtenido correctamente")
    })
    public ResponseEntity<CollectionModel<ClaseDTO>> getByCurso(
            @Parameter(description = "Identificador del curso") @PathVariable Long idCurso) {
        List<ClaseDTO> clasesDto = claseService.getByCurso(idCurso).stream()
                .map(ClaseDTO::fromModel)
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<ClaseDTO> collectionModel = CollectionModel.of(clasesDto);
        collectionModel.add(linkTo(methodOn(ClaseController.class).getByCurso(idCurso)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
    @PostMapping
    @Operation(summary = "Crear clase", description = "Registra una nueva clase")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Clase creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la clase inválidos")
    })
    public ResponseEntity<ClaseDTO> crear(@Valid @RequestBody ClaseDTO claseDto) {
        Clase guardada = claseService.guardar(claseDto.toModel());
        ClaseDTO responseDto = agregarLinks(ClaseDTO.fromModel(guardada));
        
        return ResponseEntity
            .created(responseDto.getRequiredLink("self").toUri())
            .body(responseDto);
        
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar clase", description = "Actualiza los datos de una clase existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clase actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la clase inválidos"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<ClaseDTO> actualizar(
            @Parameter(description = "Identificador de la clase a actualizar") @PathVariable Long id,
            @Valid @RequestBody ClaseDTO claseDto) {
        return claseService.getById(id)
                .map(existing -> {
                    claseDto.setIdClase(id);
                    Clase actualizada = claseService.guardar(claseDto.toModel());
                    ClaseDTO responseDto = agregarLinks(ClaseDTO.fromModel(actualizada));
                    return ResponseEntity.ok(responseDto);

                })
                .orElse(ResponseEntity.notFound().build());
            }
                    
                    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar clase", description = "Elimina una clase a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Clase eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Clase no encontrada")
    })
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador de la clase a eliminar") @PathVariable Long id) {
        return claseService.getById(id)
                .map(existing -> {
                    claseService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private ClaseDTO agregarLinks(ClaseDTO dto) {
        dto.add(linkTo(methodOn(ClaseController.class).getById(dto.getIdClase())).withSelfRel());
        dto.add(linkTo(methodOn(ClaseController.class).getAll()).withRel("clases"));
        return dto;
    }


}