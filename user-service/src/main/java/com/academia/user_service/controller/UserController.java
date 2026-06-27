package com.academia.user_service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.academia.user_service.model.User;
import com.academia.user_service.service.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/usuarios/listar")
    public List<User> listar() {
        return userService.listarTodo();
    }

    @GetMapping("/usuarios/buscar/{id}")
    public ResponseEntity<User> buscar(@PathVariable Long id) {
        User user = userService.buscarPorId(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping("/usuarios/crear")
    public ResponseEntity<User> crear(@RequestBody User user) {
        return ResponseEntity.ok(userService.guardarUsuario(user));
    }

    @PutMapping("/usuarios/actualizar/{id}")
    public ResponseEntity<User> actualizar(@PathVariable Long id, @RequestBody User userDetails) {
        User actualizado = userService.actualizarUsuario(id, userDetails);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/usuarios/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable Long id) {
        boolean eliminado = userService.eliminarUsuario(id);
        return eliminado ? ResponseEntity.ok("Usuario eliminado") : ResponseEntity.notFound().build();
    }
}
