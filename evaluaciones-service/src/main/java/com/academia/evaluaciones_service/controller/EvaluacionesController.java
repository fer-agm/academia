package com.academia.evaluaciones_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Operation(summary = "Listar evaluaciones", description = "Obtiene la lista completa de evaluaciones registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de evaluaciones obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<Evaluaciones>> getAllEvaluaciones() {
        return ResponseEntity.ok(evaluacionesService.getAllEvaluaciones());
    }

    @Operation(summary = "Obtener evaluación por ID", description = "Obtiene una evaluación específica a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación encontrada"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Evaluaciones> getEvaluacionById(
            @Parameter(description = "Identificador de la evaluación") @PathVariable Long id) {
        return evaluacionesService.getEvaluacionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear evaluación", description = "Registra una nueva evaluación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la evaluación inválidos")
    })
    @PostMapping
    public ResponseEntity<Evaluaciones> crearEvaluacion(@Valid @RequestBody Evaluaciones evaluaciones) {
        return ResponseEntity.ok(evaluacionesService.guardar(evaluaciones));
    }

    @Operation(summary = "Actualizar evaluación", description = "Actualiza una evaluación existente identificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Evaluación actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la evaluación inválidos"),
            @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Evaluaciones> actualizar(
            @Parameter(description = "Identificador de la evaluación a actualizar") @PathVariable Long id,
            @Valid @RequestBody Evaluaciones evaluaciones) {
        return evaluacionesService.getEvaluacionById(id)
                .map(existing -> {
                    evaluaciones.setIdEvaluacion(id);
                    return ResponseEntity.ok(evaluacionesService.guardar(evaluaciones));
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
