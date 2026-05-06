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
        return userRepository.save(user);
    }

    public List<User> ListarTodo() {
        return userRepository.findAll();
    }

    public User buscarPorRun (String run) {
        return userRepository.findById(run).orElse (null);
    }

    
}
