package com.academia.evaluaciones_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import com.academia.evaluaciones_service.model.Evaluaciones;
import com.academia.evaluaciones_service.service.EvaluacionesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/evaluaciones")
@Tag(name = "Evaluaciones", description = "Gestión de las evaluaciones asociadas a los cursos")
public class EvaluacionesController {

    private final EvaluacionesService evaluacionesService;

    public EvaluacionesController(EvaluacionesService evaluacionesService) {
        this.evaluacionesService = evaluacionesService;
    }

    private EntityModel<Evaluaciones> toModel(Evaluaciones evaluacion) {
        return EntityModel.of(evaluacion,
                linkTo(methodOn(EvaluacionesController.class).getEvaluacionById(evaluacion.getIdEvaluacion())).withSelfRel(),
                linkTo(methodOn(EvaluacionesController.class).getAllEvaluaciones()).withRel("listar"));
    }

    @Operation(summary = "Listar evaluaciones", description = "Obtiene la lista completa de evaluaciones registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de evaluaciones obtenida correctamente")
    })
    @GetMapping
    public CollectionModel<EntityModel<Evaluaciones>> getAllEvaluaciones() {
        List<EntityModel<Evaluaciones>> evaluaciones = evaluacionesService.getAllEvaluaciones().stream()
                .map(this::toModel)
                .toList();
        return CollectionModel.of(evaluaciones,
                linkTo(methodOn(EvaluacionesController.class).getAllEvaluaciones()).withSelfRel());
    }

    @Operation(summary = "Obtener evaluación por ID", description = "Obtiene una evaluación específica a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación encontrada"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Evaluaciones>> getEvaluacionById(
            @Parameter(description = "Identificador de la evaluación") @PathVariable Long id) {
        return evaluacionesService.getEvaluacionById(id)
                .map(this::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Verificar existencia de evaluación",
            description = "Indica si existe una evaluación con el identificador indicado. "
                    + "Endpoint pensado para validaciones entre microservicios.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación realizada correctamente")
    })
    @GetMapping("/{id}/existe")
    public ResponseEntity<Boolean> existe(
            @Parameter(description = "Identificador de la evaluación a verificar") @PathVariable Long id) {
        return ResponseEntity.ok(evaluacionesService.existe(id));
    }

    @Operation(summary = "Crear evaluación",
            description = "Registra una nueva evaluación. Valida la existencia del curso asociado antes de persistir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la evaluación inválidos o curso no existente")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Evaluaciones>> crearEvaluacion(@Valid @RequestBody Evaluaciones evaluaciones,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        Evaluaciones saved = evaluacionesService.guardar(evaluaciones, authHeader);
        return ResponseEntity.ok(EntityModel.of(saved,
                linkTo(methodOn(EvaluacionesController.class).getEvaluacionById(saved.getIdEvaluacion())).withSelfRel()));
    }

    @Operation(summary = "Actualizar evaluación", description = "Actualiza una evaluación existente identificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la evaluación inválidos"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Evaluaciones>> actualizar(
            @Parameter(description = "Identificador de la evaluación a actualizar") @PathVariable Long id,
            @Valid @RequestBody Evaluaciones evaluaciones,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return evaluacionesService.getEvaluacionById(id)
                .map(existing -> {
                    evaluaciones.setIdEvaluacion(id);
                    Evaluaciones saved = evaluacionesService.guardar(evaluaciones, authHeader);
                    return ResponseEntity.ok(EntityModel.of(saved,
                            linkTo(methodOn(EvaluacionesController.class).getEvaluacionById(saved.getIdEvaluacion())).withSelfRel()));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar evaluación", description = "Elimina una evaluación existente identificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Evaluación eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador de la evaluación a eliminar") @PathVariable Long id){
        if (evaluacionesService.getEvaluacionById(id).isPresent()) {
            evaluacionesService.borrar(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
