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

import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.service.AlternativasService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/alternativas")
@Tag(name = "Alternativas", description = "Gestión de las alternativas de respuesta de las preguntas")
public class AlternativasController {

    private final AlternativasService alternativasService;

    public AlternativasController(AlternativasService alternativasService) {
        this.alternativasService = alternativasService;
    }

    @Operation(summary = "Listar alternativas", description = "Obtiene la lista completa de alternativas registradas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alternativas obtenida correctamente")
    })
    @GetMapping
    public ResponseEntity<List<Alternativas>> getAll() {
        return ResponseEntity.ok(alternativasService.getAll());
    }

    @Operation(summary = "Obtener alternativa por ID", description = "Obtiene una alternativa específica a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alternativa encontrada"),
            @ApiResponse(responseCode = "404", description = "Alternativa no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Alternativas> getById(
            @Parameter(description = "Identificador de la alternativa") @PathVariable Long id) {
        return alternativasService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear alternativa", description = "Registra una nueva alternativa de respuesta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alternativa creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la alternativa inválidos")
    })
    @PostMapping
    public ResponseEntity<Alternativas> crear(@Valid @RequestBody Alternativas alternativas) {
        return ResponseEntity.ok(alternativasService.guardar(alternativas));
    }

    @Operation(summary = "Actualizar alternativa", description = "Actualiza una alternativa existente identificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Alternativa actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de la alternativa inválidos"),
            @ApiResponse(responseCode = "404", description = "Alternativa no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Alternativas> actualizar(
            @Parameter(description = "Identificador de la alternativa a actualizar") @PathVariable Long id,
            @Valid @RequestBody Alternativas alternativas) {
        return alternativasService.getById(id)
                .map(existing -> {
                    alternativas.setIdAlternativa(id);
                    return ResponseEntity.ok(alternativasService.guardar(alternativas));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar alternativa", description = "Elimina una alternativa existente identificada por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Alternativa eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Alternativa no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador de la alternativa a eliminar") @PathVariable Long id) {
        return alternativasService.getById(id)
                .map(existing -> {
                    alternativasService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
