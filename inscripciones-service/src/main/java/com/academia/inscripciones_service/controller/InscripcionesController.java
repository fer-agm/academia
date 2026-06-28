package com.academia.inscripciones_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "Inscripciones", description = "Operaciones para la gestión de inscripciones de estudiantes en cursos")
public class InscripcionesController {

    @Autowired
    private InscripcionesService inscripcionesService;

    private EntityModel<Inscripciones> toModel(Inscripciones ins) {
        return EntityModel.of(ins,
                linkTo(methodOn(InscripcionesController.class).obtenerPorId(ins.getId_inscripcion())).withSelfRel(),
                linkTo(methodOn(InscripcionesController.class).listar()).withRel("listar"));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todas las inscripciones", description = "Devuelve la lista completa de inscripciones registradas en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de inscripciones obtenida correctamente")
    })
    public CollectionModel<EntityModel<Inscripciones>> listar() {
        List<EntityModel<Inscripciones>> inscripciones = inscripcionesService.listarTodas().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(inscripciones,
                linkTo(methodOn(InscripcionesController.class).listar()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener inscripción por ID", description = "Devuelve la inscripción correspondiente al identificador indicado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción encontrada"),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public ResponseEntity<EntityModel<Inscripciones>> obtenerPorId(
            @Parameter(description = "Identificador único de la inscripción", example = "1") @PathVariable Long id) {
        Inscripciones ins = inscripcionesService.buscarPorId(id);
        return ins != null ? ResponseEntity.ok(toModel(ins)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/estudiante/{run}")
    @Operation(summary = "Listar inscripciones por estudiante", description = "Devuelve todas las inscripciones asociadas al RUN del estudiante indicado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripciones del estudiante obtenidas correctamente")
    })
    public ResponseEntity<CollectionModel<EntityModel<Inscripciones>>> obtenerPorEstudiante(
            @Parameter(description = "RUN del estudiante", example = "12345678-9") @PathVariable String run) {
        List<EntityModel<Inscripciones>> inscripciones = inscripcionesService.listarPorEstudiante(run).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        CollectionModel<EntityModel<Inscripciones>> model = CollectionModel.of(inscripciones,
                linkTo(methodOn(InscripcionesController.class).obtenerPorEstudiante(run)).withSelfRel(),
                linkTo(methodOn(InscripcionesController.class).listar()).withRel("listar"));
        return ResponseEntity.ok(model);
    }

    @PostMapping("/inscribir")
    @Operation(summary = "Crear inscripción", description = "Inscribe a un estudiante en un curso. La fecha y el estado se asignan automáticamente si no se indican")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción creada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<EntityModel<Inscripciones>> inscribir(
            @Valid @RequestBody Inscripciones inscripciones,
            @RequestHeader(value = org.springframework.http.HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        Inscripciones creada = inscripcionesService.crearInscripcion(inscripciones, authHeader);
        EntityModel<Inscripciones> model = EntityModel.of(creada,
                linkTo(methodOn(InscripcionesController.class).obtenerPorId(creada.getId_inscripcion())).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar inscripción", description = "Actualiza el estado y el curso de una inscripción existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inscripción actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Inscripción no encontrada")
    })
    public ResponseEntity<EntityModel<Inscripciones>> actualizar(
            @Parameter(description = "Identificador único de la inscripción", example = "1") @PathVariable Long id,
            @Valid @RequestBody Inscripciones datos) {
        Inscripciones actualizada = inscripcionesService.actualizar(id, datos);
        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<Inscripciones> model = EntityModel.of(actualizada,
                linkTo(methodOn(InscripcionesController.class).obtenerPorId(actualizada.getId_inscripcion())).withSelfRel());
        return ResponseEntity.ok(model);
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
