package com.academia.pago_service.controller;

import com.academia.pago_service.model.Transaccion;
import com.academia.pago_service.service.TransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Transacciones", description = "Gestión de transacciones asociadas a pagos")
@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @Operation(summary = "Listar todas las transacciones",
            description = "Devuelve la lista completa de transacciones registradas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de transacciones obtenida correctamente")
    })
    @GetMapping("/listar")
    public List<Transaccion> listar() {
        return transaccionService.listarTodas();
    }

    @Operation(summary = "Obtener una transacción por ID",
            description = "Devuelve la transacción correspondiente al identificador indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción encontrada"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transaccion> buscarPorId(
            @Parameter(description = "Identificador único de la transacción") @PathVariable Long id) {
        Transaccion t = transaccionService.buscarPorId(id);
        return t != null ? ResponseEntity.ok(t) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Generar una nueva transacción",
            description = "Registra una nueva transacción. La fecha se asigna automáticamente si no se indica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/generar")
    public ResponseEntity<Transaccion> generar(@Valid @RequestBody Transaccion transaccion) {
        return ResponseEntity.ok(transaccionService.registrarTransaccion(transaccion));
    }

    @Operation(summary = "Actualizar una transacción",
            description = "Actualiza el método de pago de la transacción indicada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Transaccion> actualizar(
            @Parameter(description = "Identificador único de la transacción a actualizar") @PathVariable Long id,
            @Valid @RequestBody Transaccion t) {
        Transaccion actualizada = transaccionService.actualizarTransaccion(id, t);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una transacción",
            description = "Elimina la transacción correspondiente al identificador indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transacción eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador único de la transacción a eliminar") @PathVariable Long id) {
        return transaccionService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}