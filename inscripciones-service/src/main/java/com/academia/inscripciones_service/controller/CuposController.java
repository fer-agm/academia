package com.academia.inscripciones_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.academia.inscripciones_service.model.Cupos;
import com.academia.inscripciones_service.service.CuposService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cupos")
@Tag(name = "Cupos", description = "Operaciones para la gestión de cupos de los cursos")
public class CuposController {

    @Autowired
    private CuposService cuposService;

    private EntityModel<Cupos> toModel(Cupos cupo) {
        return EntityModel.of(cupo,
                linkTo(methodOn(CuposController.class).obtenerPorId(cupo.getId_cupo())).withSelfRel(),
                linkTo(methodOn(CuposController.class).listar()).withRel("listar"));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todos los cupos", description = "Devuelve la lista completa de registros de cupos registrados en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de cupos obtenida correctamente")
    })
    public CollectionModel<EntityModel<Cupos>> listar() {
        List<EntityModel<Cupos>> cupos = cuposService.listarTodos().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(cupos,
                linkTo(methodOn(CuposController.class).listar()).withSelfRel());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cupo por ID", description = "Devuelve el registro de cupos correspondiente al identificador indicado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cupo encontrado"),
        @ApiResponse(responseCode = "404", description = "Cupo no encontrado")
    })
    public ResponseEntity<EntityModel<Cupos>> obtenerPorId(
            @Parameter(description = "Identificador único del registro de cupos", example = "1") @PathVariable Long id) {
        Cupos cupo = cuposService.buscarPorId(id);
        return cupo != null ? ResponseEntity.ok(toModel(cupo)) : ResponseEntity.notFound().build();
    }

    @GetMapping("/curso/{idCurso}")
    @Operation(summary = "Consultar cupos por curso", description = "Devuelve el registro de cupos asociado al curso indicado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cupos del curso encontrados"),
        @ApiResponse(responseCode = "404", description = "No existen cupos para el curso indicado")
    })
    public ResponseEntity<EntityModel<Cupos>> consultarPorCurso(
            @Parameter(description = "Identificador del curso", example = "10") @PathVariable Long idCurso) {
        Cupos cupo = cuposService.obtenerPorCurso(idCurso);
        if (cupo == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<Cupos> model = EntityModel.of(cupo,
                linkTo(methodOn(CuposController.class).consultarPorCurso(idCurso)).withSelfRel(),
                linkTo(methodOn(CuposController.class).obtenerPorId(cupo.getId_cupo())).withRel("cupo"),
                linkTo(methodOn(CuposController.class).listar()).withRel("listar"));
        return ResponseEntity.ok(model);
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear cupo", description = "Crea un nuevo registro de cupos para un curso")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cupo creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<EntityModel<Cupos>> crear(@Valid @RequestBody Cupos cupo) {
        Cupos guardado = cuposService.guardarCupo(cupo);
        EntityModel<Cupos> model = EntityModel.of(guardado,
                linkTo(methodOn(CuposController.class).obtenerPorId(guardado.getId_cupo())).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar cupo", description = "Actualiza el número máximo y disponible de cupos de un registro existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cupo actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "404", description = "Cupo no encontrado")
    })
    public ResponseEntity<EntityModel<Cupos>> actualizar(
            @Parameter(description = "Identificador único del registro de cupos", example = "1") @PathVariable Long id,
            @Valid @RequestBody Cupos cupo) {
        Cupos actualizado = cuposService.actualizar(id, cupo);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<Cupos> model = EntityModel.of(actualizado,
                linkTo(methodOn(CuposController.class).obtenerPorId(actualizado.getId_cupo())).withSelfRel());
        return ResponseEntity.ok(model);
    }


    @DeleteMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar cupo", description = "Elimina el registro de cupos correspondiente al identificador indicado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cupo eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Cupo no encontrado")
    })
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador único del registro de cupos", example = "1") @PathVariable Long id) {
        return cuposService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/descontar/{idCurso}")
    @Operation(summary = "Descontar un cupo", description = "Reduce en una unidad los cupos disponibles del curso indicado, si existen cupos disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cupo descontado exitosamente"),
        @ApiResponse(responseCode = "400", description = "No hay cupos disponibles")
    })
    public ResponseEntity<String> descontar(
            @Parameter(description = "Identificador del curso", example = "10") @PathVariable Long idCurso) {
        boolean resultado = cuposService.reducirCupo(idCurso);
        return resultado ? ResponseEntity.ok("Cupo descontado exitosamente")
                         : ResponseEntity.badRequest().body("No hay cupos disponibles");
    }
}
