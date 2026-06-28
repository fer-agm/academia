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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

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
    public CollectionModel<EntityModel<Transaccion>> listar() {
        List<EntityModel<Transaccion>> transacciones = transaccionService.listarTodas().stream()
                .map(t -> EntityModel.of(t,
                        linkTo(methodOn(TransaccionController.class).buscarPorId(t.getId_transaccion())).withSelfRel()))
                .collect(Collectors.toList());
        return CollectionModel.of(transacciones,
                linkTo(methodOn(TransaccionController.class).listar()).withSelfRel());
    }

    @Operation(summary = "Obtener una transacción por ID",
            description = "Devuelve la transacción correspondiente al identificador indicado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción encontrada"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Transaccion>> buscarPorId(
            @Parameter(description = "Identificador único de la transacción") @PathVariable Long id) {
        Transaccion t = transaccionService.buscarPorId(id);
        if (t == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<Transaccion> model = EntityModel.of(t,
                linkTo(methodOn(TransaccionController.class).buscarPorId(t.getId_transaccion())).withSelfRel(),
                linkTo(methodOn(TransaccionController.class).listar()).withRel("listar"));
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Generar una nueva transacción",
            description = "Registra una nueva transacción. La fecha se asigna automáticamente si no se indica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción registrada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping("/generar")
    public ResponseEntity<EntityModel<Transaccion>> generar(@Valid @RequestBody Transaccion transaccion) {
        Transaccion guardada = transaccionService.registrarTransaccion(transaccion);
        EntityModel<Transaccion> model = EntityModel.of(guardada,
                linkTo(methodOn(TransaccionController.class).buscarPorId(guardada.getId_transaccion())).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Actualizar una transacción",
            description = "Actualiza el método de pago de la transacción indicada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<EntityModel<Transaccion>> actualizar(
            @Parameter(description = "Identificador único de la transacción a actualizar") @PathVariable Long id,
            @Valid @RequestBody Transaccion t) {
        Transaccion actualizada = transaccionService.actualizarTransaccion(id, t);
        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<Transaccion> model = EntityModel.of(actualizada,
                linkTo(methodOn(TransaccionController.class).buscarPorId(actualizada.getId_transaccion())).withSelfRel());
        return ResponseEntity.ok(model);
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