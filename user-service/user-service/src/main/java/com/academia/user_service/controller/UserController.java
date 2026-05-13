package com.academia.user_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.user_service.model.User;
import com.academia.user_service.service.UserService;



@RestController
@RequestMapping("/usuarios") // Este es el path que configuramos en el Gateway
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/crear")
    public User crear(@RequestBody User user) {
        return userService.guardarUsuario(user);
    }

    @GetMapping("/listar")
    public List<User> listar() {
        return userService.listarTodo();
    }

    @GetMapping("/buscar/{id}")
    public User buscar(@PathVariable String id) {
        return userService.buscarPorId(id);
    }
}


