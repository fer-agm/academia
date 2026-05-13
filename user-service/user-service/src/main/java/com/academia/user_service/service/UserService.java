package com.academia.user_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.academia.user_service.model.User;
import com.academia.user_service.repository.UserRepository;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User guardarUsuario(User user) {
        // Si la fecha de registro es nula, le asignamos la fecha actual del sistema
        if (user.getFecha_Registro() == null) {
            user.setFecha_Registro(new java.sql.Date(System.currentTimeMillis()));
        }
        return userRepository.save(user);
    }

    public List<User> listarTodo() {
        return userRepository.findAll();
    }

    public User buscarPorId(String id) {
        return userRepository.findById(id).orElse(null);
    }

    public User buscarPorRun(String run) {
        // Como el RUN no es el @Id, 
        return listarTodo().stream()
                .filter(u -> u.getRun().equals(run))
                .findFirst()
                .orElse(null);
    }

    
}
