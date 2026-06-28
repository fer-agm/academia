package com.academia.user_service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<User> listar() {
        return userService.listarTodo();
    }

    @Operation(summary = "Buscar usuario por ID", description = "Obtiene un usuario a partir de su identificador")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/usuarios/buscar/{id}")
    public ResponseEntity<User> buscar(
            @Parameter(description = "Identificador del usuario") @PathVariable Long id) {
        User user = userService.buscarPorId(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario y provisiona su credencial de acceso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del usuario inválidos")
    })
    @PostMapping("/usuarios/crear")
    public ResponseEntity<User> crear(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.guardarUsuario(user));
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del usuario inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/usuarios/actualizar/{id}")
    public ResponseEntity<User> actualizar(
            @Parameter(description = "Identificador del usuario a actualizar") @PathVariable Long id,
            @Valid @RequestBody User userDetails) {
        User actualizado = userService.actualizarUsuario(id, userDetails);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
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
