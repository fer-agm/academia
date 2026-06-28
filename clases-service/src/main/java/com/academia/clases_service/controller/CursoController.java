package com.academia.clases_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

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

import com.academia.clases_service.model.Curso;
import com.academia.clases_service.service.CursoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "Cursos", description = "Operaciones para la gestión de cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping ("/listar")
    @Operation(summary = "Listar cursos", description = "Obtiene la lista de todos los cursos registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de cursos obtenido correctamente")
    })
    public ResponseEntity<CollectionModel<EntityModel<Curso>>> getAll() {
        List<EntityModel<Curso>> cursos = cursoService.getAll().stream()
                .map(curso -> EntityModel.of(curso,
                        linkTo(methodOn(CursoController.class).getById(curso.getIdCurso())).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Curso>> collectionModel = CollectionModel.of(cursos,
                linkTo(methodOn(CursoController.class).getAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener curso por ID", description = "Busca y devuelve un curso a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso encontrado"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public ResponseEntity<EntityModel<Curso>> getById(
            @Parameter(description = "Identificador del curso") @PathVariable Long id) {
        return cursoService.getById(id)
                .map(curso -> EntityModel.of(curso,
                        linkTo(methodOn(CursoController.class).getById(id)).withSelfRel(),
                        linkTo(methodOn(CursoController.class).getAll()).withRel("listar")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{idCategoria}")
    @Operation(summary = "Listar cursos por categoría", description = "Obtiene los cursos que pertenecen a una categoría determinada")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de cursos de la categoría obtenido correctamente")
    })
    public ResponseEntity<CollectionModel<EntityModel<Curso>>> getByCategoria(
            @Parameter(description = "Identificador de la categoría") @PathVariable Long idCategoria) {
        List<EntityModel<Curso>> cursos = cursoService.getByCategoria(idCategoria).stream()
                .map(curso -> EntityModel.of(curso,
                        linkTo(methodOn(CursoController.class).getById(curso.getIdCurso())).withSelfRel()))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<Curso>> collectionModel = CollectionModel.of(cursos,
                linkTo(methodOn(CursoController.class).getByCategoria(idCategoria)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}/existe")
    @Operation(summary = "Verificar existencia de curso", description = "Indica si existe un curso con el identificador indicado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificación realizada correctamente")
    })
    public ResponseEntity<Boolean> existe(
            @Parameter(description = "Identificador del curso a verificar") @PathVariable Long id) {
        return ResponseEntity.ok(cursoService.getById(id).isPresent());
    }

    @PostMapping
    @Operation(summary = "Crear curso", description = "Registra un nuevo curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del curso inválidos")
    })
    public ResponseEntity<EntityModel<Curso>> crear(@Valid @RequestBody Curso curso) {
        Curso guardado = cursoService.guardar(curso);
        EntityModel<Curso> model = EntityModel.of(guardado,
                linkTo(methodOn(CursoController.class).getById(guardado.getIdCurso())).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar curso", description = "Actualiza los datos de un curso existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Curso actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del curso inválidos"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public ResponseEntity<EntityModel<Curso>> actualizar(
            @Parameter(description = "Identificador del curso a actualizar") @PathVariable Long id,
            @Valid @RequestBody Curso curso) {
        return cursoService.getById(id)
                .map(existing -> {
                    curso.setIdCurso(id);
                    Curso guardado = cursoService.guardar(curso);
                    EntityModel<Curso> model = EntityModel.of(guardado,
                            linkTo(methodOn(CursoController.class).getById(id)).withSelfRel());
                    return ResponseEntity.ok(model);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar curso", description = "Elimina un curso a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Curso eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Curso no encontrado")
    })
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador del curso a eliminar") @PathVariable Long id) {
        return cursoService.getById(id)
                .map(existing -> {
                    cursoService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
