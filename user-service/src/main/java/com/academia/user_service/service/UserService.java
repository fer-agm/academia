package com.academia.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.user_service.model.User;
import com.academia.user_service.repository.UserRepository;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User guardarUsuario(User user) {
        log.info("[UserService] Creando usuario con RUN: {}", user.getRun());
        if (user.getFecha_Registro() == null) {
            user.setFecha_Registro(new java.sql.Date(System.currentTimeMillis()));
        }
        User saved = userRepository.save(user);
        log.info("[UserService] Usuario creado con ID: {}", saved.getId());
        return saved;
    }

    public List<User> listarTodo() {
        log.info("[UserService] Obteniendo todos los usuarios");
        return userRepository.findAll();
    }

    public User buscarPorId(String id) {
        log.info("[UserService] Buscando usuario con ID: {}", id);
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            log.warn("[UserService] Usuario con ID {} no encontrado", id);
        }
        return user;
    }

    public User buscarPorRun(String run) {
        log.info("[UserService] Buscando usuario con RUN: {}", run);
        return userRepository.findByRun(run).orElse(null);
    }

    public User actualizarUsuario(String id, User nuevosDatos) {
        log.info("[UserService] Actualizando usuario con ID: {}", id);
        return userRepository.findById(id).map(user -> {
            user.setNombre(nuevosDatos.getNombre());
            user.setApellido(nuevosDatos.getApellido());
            user.setUsuario(nuevosDatos.getUsuario());
            user.setClave(nuevosDatos.getClave());
            user.setEmail(nuevosDatos.getEmail());
            user.setFecha_Nacimiento(nuevosDatos.getFecha_Nacimiento());
            User updated = userRepository.save(user);
            log.info("[UserService] Usuario con ID {} actualizado", id);
            return updated;
        }).orElse(null);
    }

    public boolean eliminarUsuario(String id) {
        log.info("[UserService] Eliminando usuario con ID: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("[UserService] Usuario con ID {} eliminado", id);
            return true;
        }
        log.warn("[UserService] Usuario con ID {} no encontrado para eliminar", id);
        return false;
    }
}