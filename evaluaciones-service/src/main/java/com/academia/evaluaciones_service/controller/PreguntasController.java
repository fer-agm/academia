package com.academia.evaluaciones_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.evaluaciones_service.model.Preguntas;
import com.academia.evaluaciones_service.service.PreguntasService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/preguntas")
@Tag(name = "Preguntas", description = "Gestión de las preguntas de las evaluaciones")
public class PreguntasController {

    private final PreguntasService preguntasService;

    public PreguntasController(PreguntasService preguntasService) {
        this.preguntasService = preguntasService;
    }

    private EntityModel<Preguntas> toModel(Preguntas pregunta) {
        return EntityModel.of(pregunta,
                linkTo(methodOn(PreguntasController.class).getById(pregunta.getIdPregunta())).withSelfRel(),
                linkTo(methodOn(PreguntasController.class).getAll()).withRel("listar"));
    }

    @Operation(summary = "Listar preguntas", description = "Obtiene la lista completa de preguntas registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de preguntas obtenida correctamente")
    })
    @GetMapping
    public CollectionModel<EntityModel<Preguntas>> getAll() {
        List<EntityModel<Preguntas>> preguntas = preguntasService.getAll().stream()
                .map(this::toModel)
                .toList();
        return CollectionModel.of(preguntas,
                linkTo(methodOn(PreguntasController.class).getAll()).withSelfRel());
    }

    @Operation(summary = "Obtener pregunta por ID", description = "Obtiene una pregunta específica a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregunta encontrada"),
            @ApiResponse(responseCode = "404", description = "Pregunta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Preguntas>> getById(
            @Parameter(description = "Identificador de la pregunta") @PathVariable Long id) {
        return preguntasService.getById(id)
                .map(this::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear pregunta", description = "Registra una nueva pregunta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregunta creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la pregunta inválidos")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Preguntas>> crear(@Valid @RequestBody Preguntas preguntas) {
        Preguntas saved = preguntasService.guardar(preguntas);
        return ResponseEntity.ok(EntityModel.of(saved,
                linkTo(methodOn(PreguntasController.class).getById(saved.getIdPregunta())).withSelfRel()));
    }

    @Operation(summary = "Actualizar pregunta", description = "Actualiza una pregunta existente identificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregunta actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la pregunta inválidos"),
            @ApiResponse(responseCode = "404", description = "Pregunta no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Preguntas>> actualizar(
            @Parameter(description = "Identificador de la pregunta a actualizar") @PathVariable Long id,
            @Valid @RequestBody Preguntas preguntas) {
        return preguntasService.getById(id)
                .map(existing -> {
                    preguntas.setIdPregunta(id);
                    Preguntas saved = preguntasService.guardar(preguntas);
                    return ResponseEntity.ok(EntityModel.of(saved,
                            linkTo(methodOn(PreguntasController.class).getById(saved.getIdPregunta())).withSelfRel()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar pregunta", description = "Elimina una pregunta existente identificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pregunta eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Pregunta no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador de la pregunta a eliminar") @PathVariable Long id) {
        return preguntasService.getById(id)
                .map(existing -> {
                    preguntasService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
