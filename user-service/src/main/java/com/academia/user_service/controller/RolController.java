package com.academia.user_service.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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
    public CollectionModel<EntityModel<Rol>> listar() {
        List<EntityModel<Rol>> roles = rolService.listarTodos().stream()
                .map(rol -> EntityModel.of(rol,
                        linkTo(methodOn(RolController.class).obtenerPorId(rol.getId_rol())).withSelfRel()))
                .collect(Collectors.toList());
        return CollectionModel.of(roles,
                linkTo(methodOn(RolController.class).listar()).withSelfRel());
    }

    @Operation(summary = "Obtener rol por ID", description = "Obtiene un rol a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol encontrado"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Rol>> obtenerPorId(
            @Parameter(description = "Identificador del rol") @PathVariable Long id) {
        Rol rol = rolService.buscarPorId(id);
        if (rol == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<Rol> model = EntityModel.of(rol,
                linkTo(methodOn(RolController.class).obtenerPorId(rol.getId_rol())).withSelfRel(),
                linkTo(methodOn(RolController.class).listar()).withRel("listar"));
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Crear rol", description = "Crea un nuevo rol")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del rol inválidos")
    })
    @PostMapping("/crear")
    public ResponseEntity<EntityModel<Rol>> crear(@Valid @RequestBody Rol rol) {
        Rol saved = rolService.guardarRol(rol);
        EntityModel<Rol> model = EntityModel.of(saved,
                linkTo(methodOn(RolController.class).obtenerPorId(saved.getId_rol())).withSelfRel());
        return ResponseEntity.ok(model);
    }


    @Operation(summary = "Actualizar rol", description = "Actualiza los datos de un rol existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del rol inválidos"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado")
    })
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<EntityModel<Rol>> actualizar(
            @Parameter(description = "Identificador del rol a actualizar") @PathVariable Long id,
            @Valid @RequestBody Rol rol) {
        Rol actualizado = rolService.actualizarRol(id, rol);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<Rol> model = EntityModel.of(actualizado,
                linkTo(methodOn(RolController.class).obtenerPorId(actualizado.getId_rol())).withSelfRel());
        return ResponseEntity.ok(model);
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
