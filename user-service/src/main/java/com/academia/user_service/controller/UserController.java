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
    public ResponseEntity<User> buscar(@PathVariable String id) {
        User user = userService.buscarPorId(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping("/usuarios/crear")
    public ResponseEntity<User> crear(@RequestBody User user) {
        return ResponseEntity.ok(userService.guardarUsuario(user));
    }

    @PutMapping("/usuarios/actualizar/{id}")
    public ResponseEntity<User> actualizar(@PathVariable String id, @RequestBody User userDetails) {
        User actualizado = userService.actualizarUsuario(id, userDetails);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/usuarios/eliminar/{id}")
    public ResponseEntity<String> eliminar(@PathVariable String id) {
        boolean eliminado = userService.eliminarUsuario(id);
        return eliminado ? ResponseEntity.ok("Usuario eliminado") : ResponseEntity.notFound().build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody User loginDetails) {
        User user = userService.buscarPorRun(loginDetails.getRun());
        
        if (user != null && user.getClave().equals(loginDetails.getClave())) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).body("RUN o clave incorrectos");
    }
}