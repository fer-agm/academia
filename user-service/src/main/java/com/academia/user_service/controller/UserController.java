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
import com.academia.user_service.model.User;
import com.academia.user_service.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Tag(name = "Usuarios", description = "Operaciones de gestión de usuarios")
public class UserController {

    @Autowired
    private UserService userService;


    @Operation(summary = "Listar usuarios", description = "Obtiene la lista de todos los usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente")
    })
    @GetMapping("/usuarios/listar")
    public CollectionModel<EntityModel<User>> listar() {
        List<EntityModel<User>> usuarios = userService.listarTodo().stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).buscar(user.getId())).withSelfRel()))
                .collect(Collectors.toList());
        return CollectionModel.of(usuarios,
                linkTo(methodOn(UserController.class).listar()).withSelfRel());
    }

    @Operation(summary = "Buscar usuario por ID", description = "Obtiene un usuario a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/usuarios/buscar/{id}")
    public ResponseEntity<EntityModel<User>> buscar(
            @Parameter(description = "Identificador del usuario") @PathVariable Long id) {
        User user = userService.buscarPorId(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<User> model = EntityModel.of(user,
                linkTo(methodOn(UserController.class).buscar(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).listar()).withRel("listar"));
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Verificar existencia de usuario por RUN", description = "Indica si existe un usuario registrado con el RUN indicado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resultado de la verificación (true/false)")
    })
    @GetMapping("/usuarios/run/{run}/existe")
    public ResponseEntity<Boolean> existePorRun(
            @Parameter(description = "RUN del usuario a verificar") @PathVariable String run) {
        return ResponseEntity.ok(userService.existePorRun(run));
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario. Endpoint público (no requiere token); el usuario podrá iniciar sesión con su run y clave.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del usuario inválidos")
    })
    @PostMapping("/usuarios/crear")
    public ResponseEntity<EntityModel<User>> crear(@Valid @RequestBody User user) {
        User saved = userService.guardarUsuario(user);
        EntityModel<User> model = EntityModel.of(saved,
                linkTo(methodOn(UserController.class).buscar(saved.getId())).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del usuario inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/usuarios/actualizar/{id}")
    public ResponseEntity<EntityModel<User>> actualizar(
            @Parameter(description = "Identificador del usuario a actualizar") @PathVariable Long id,
            @Valid @RequestBody User userDetails) {
        User actualizado = userService.actualizarUsuario(id, userDetails);
        if (actualizado == null) {
            return ResponseEntity.notFound().build();
        }
        EntityModel<User> model = EntityModel.of(actualizado,
                linkTo(methodOn(UserController.class).buscar(actualizado.getId())).withSelfRel());
        return ResponseEntity.ok(model);
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/usuarios/eliminar/{id}")
    public ResponseEntity<String> eliminar(
            @Parameter(description = "Identificador del usuario a eliminar") @PathVariable Long id) {
        boolean eliminado = userService.eliminarUsuario(id);
        return eliminado ? ResponseEntity.ok("Usuario eliminado") : ResponseEntity.notFound().build();
    }
}
