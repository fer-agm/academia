package com.academia.user_service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.academia.user_service.model.Rol;
import com.academia.user_service.service.RolService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Operaciones de gestión de roles")
public class RolController {

    @Autowired
    private RolService rolService;

    @Operation(summary = "Listar roles", description = "Obtiene la lista de todos los roles registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de roles obtenida correctamente")
    })
    @GetMapping("/listar")
    public List<Rol> listar() {
        return rolService.listarTodos();
    }

    @Operation(summary = "Obtener rol por ID", description = "Obtiene un rol a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtenerPorId(
            @Parameter(description = "Identificador del rol") @PathVariable Long id) {
        Rol rol = rolService.buscarPorId(id);
        return rol != null ? ResponseEntity.ok(rol) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear rol", description = "Crea un nuevo rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del rol inválidos")
    })
    @PostMapping("/crear")
    public ResponseEntity<Rol> crear(@Valid @RequestBody Rol rol) {
        return ResponseEntity.ok(rolService.guardarRol(rol));
    }


    @Operation(summary = "Actualizar rol", description = "Actualiza los datos de un rol existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del rol inválidos"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Rol> actualizar(
            @Parameter(description = "Identificador del rol a actualizar") @PathVariable Long id,
            @Valid @RequestBody Rol rol) {
        Rol actualizado = rolService.actualizarRol(id, rol);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar rol", description = "Elimina un rol a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rol eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(
            @Parameter(description = "Identificador del rol a eliminar") @PathVariable Long id) {
        boolean eliminado = rolService.eliminarRol(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
