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

    // --- GESTIÓN DE USUARIOS (CRUD) ---

    // 1. GET - Listar todos los registros
    @GetMapping("/usuarios/listar")
    public List<User> listar() {
        return userService.listarTodo();
    }

    // 2. GET - Buscar por ID
    @GetMapping("/usuarios/buscar/{id}")
    public ResponseEntity<User> buscar(@PathVariable String id) {
        User user = userService.buscarPorId(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    // 3. POST - Crear registro
    @PostMapping("/usuarios/crear")
    public ResponseEntity<User> crear(@RequestBody User user) {
        return ResponseEntity.ok(userService.guardarUsuario(user));
    }

    // 4. PUT - Actualizar registro existente
    @PutMapping("/usuarios/actualizar/{id}")
    public ResponseEntity<User> actualizar(@PathVariable String id, @RequestBody User userDetails) {
        User actualizado = userService.actualizarUsuario(id, userDetails);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    // 5. DELETE - Eliminar registro
    @DeleteMapping("/usuarios/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable String id) {
        boolean eliminado = userService.eliminarUsuario(id);
        return eliminado ? ResponseEntity.ok("Usuario eliminado") : ResponseEntity.notFound().build();
    }

    // --- AUTENTICACIÓN (LOGIN) ---
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody User loginDetails) {
        User user = userService.buscarPorRun(loginDetails.getRun());
        
        if (user != null && user.getClave().equals(loginDetails.getClave())) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("RUN o clave incorrectos");
    }
}