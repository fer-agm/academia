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

    // --- CREATE (POST) ---
    public User guardarUsuario(User user) {
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
      
        return userRepository.findByRun(run).orElse(null);
    }

    
    public User actualizarUsuario(String id, User nuevosDatos) {
        return userRepository.findById(id).map(user -> {
            user.setNombre(nuevosDatos.getNombre());
            user.setApellido(nuevosDatos.getApellido());
            user.setUsuario(nuevosDatos.getUsuario());
            user.setClave(nuevosDatos.getClave());
            user.setEmail(nuevosDatos.getEmail());
            user.setFecha_Nacimiento(nuevosDatos.getFecha_Nacimiento());
            return userRepository.save(user);
        }).orElse(null);
    }

  
    public boolean eliminarUsuario(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}