package com.academia.inscripciones_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.academia.inscripciones_service.model.Inscripciones;
import com.academia.inscripciones_service.service.InscripcionesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones", description = "Operaciones para la gestión de inscripciones de estudiantes en cursos")
public class InscripcionesController {

    @Autowired
    private InscripcionesService inscripcionesService;

    @GetMapping("/listar")
    @Operation(summary = "Listar todas las inscripciones", description = "Devuelve la lista completa de inscripciones registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inscripciones obtenida correctamente")
    })
    public List<Inscripciones> listar() {
        return inscripcionesService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inscripción por ID", description = "Devuelve la inscripción correspondiente al identificador indicado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción encontrada"),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public ResponseEntity<Inscripciones> obtenerPorId(
            @Parameter(description = "Identificador único de la inscripción", example = "1") @PathVariable Long id) {
        Inscripciones ins = inscripcionesService.buscarPorId(id);
        return ins != null ? ResponseEntity.ok(ins) : ResponseEntity.notFound().build();
    }

    @GetMapping("/estudiante/{run}")
    @Operation(summary = "Listar inscripciones por estudiante", description = "Devuelve todas las inscripciones asociadas al RUN del estudiante indicado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripciones del estudiante obtenidas correctamente")
    })
    public ResponseEntity<List<Inscripciones>> obtenerPorEstudiante(
            @Parameter(description = "RUN del estudiante", example = "12345678-9") @PathVariable String run) {
        return ResponseEntity.ok(inscripcionesService.listarPorEstudiante(run));
    }

    @PostMapping("/inscribir")
    @Operation(summary = "Crear inscripción", description = "Inscribe a un estudiante en un curso. La fecha y el estado se asignan automáticamente si no se indican")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<Inscripciones> inscribir(@Valid @RequestBody Inscripciones inscripciones) {
        return ResponseEntity.ok(inscripcionesService.crearInscripcion(inscripciones));
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar inscripción", description = "Actualiza el estado y el curso de una inscripción existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public ResponseEntity<Inscripciones> actualizar(
            @Parameter(description = "Identificador único de la inscripción", example = "1") @PathVariable Long id,
            @Valid @RequestBody Inscripciones datos) {
        Inscripciones actualizada = inscripcionesService.actualizar(id, datos);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar inscripción", description = "Elimina la inscripción correspondiente al identificador indicado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Inscripción eliminada correctamente"),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador único de la inscripción", example = "1") @PathVariable Long id) {
        return inscripcionesService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
