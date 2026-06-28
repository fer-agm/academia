package com.academia.clases_service.controller;

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

import com.academia.clases_service.model.Categoria;
import com.academia.clases_service.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Operaciones para la gestión de categorías de cursos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping ("/listar")
    @Operation(summary = "Listar categorías", description = "Obtiene la lista de todas las categorías registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de categorías obtenido correctamente")
    })
    public ResponseEntity<List<Categoria>> getAll() {
        return ResponseEntity.ok(categoriaService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener categoría por ID", description = "Busca y devuelve una categoría a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Categoria> getById(
            @Parameter(description = "Identificador de la categoría") @PathVariable Long id) {
        return categoriaService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Crear categoría", description = "Registra una nueva categoría")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la categoría inválidos")
    })
    public ResponseEntity<Categoria> crear(@Valid @RequestBody Categoria categoria) {
        return ResponseEntity.ok(categoriaService.guardar(categoria));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar categoría", description = "Actualiza los datos de una categoría existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la categoría inválidos"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Categoria> actualizar(
            @Parameter(description = "Identificador de la categoría a actualizar") @PathVariable Long id,
            @Valid @RequestBody Categoria categoria) {
        return categoriaService.getById(id)
                .map(existing -> {
                    categoria.setIdCategoria(id);
                    return ResponseEntity.ok(categoriaService.guardar(categoria));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador de la categoría a eliminar") @PathVariable Long id) {
        return categoriaService.getById(id)
                .map(existing -> {
                    categoriaService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
