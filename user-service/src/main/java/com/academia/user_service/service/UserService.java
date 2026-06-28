package com.academia.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.academia.user_service.model.User;
import com.academia.user_service.repository.UserRepository;
import com.academia.user_service.repository.RolRepository;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RolRepository rolRepository;

    public UserService(UserRepository userRepository, RolRepository rolRepository) {
        this.userRepository = userRepository;
        this.rolRepository = rolRepository;
    }

    public User guardarUsuario(User user) {
        log.info("[UserService] Creando usuario con RUN: {}", user.getRun());
        if (user.getIdRol() == null || !rolRepository.existsById(user.getIdRol())) {
            throw new IllegalArgumentException("El rol con id " + user.getIdRol() + " no existe");
        }
        user.setId(null); // el id lo genera la BD (autoincremental); se ignora el que envíe el cliente
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

    public User buscarPorId(Long id) {
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

    public boolean existePorRun(String run) {
        return userRepository.findByRun(run).isPresent();
    }

    public User actualizarUsuario(Long id, User nuevosDatos) {
        log.info("[UserService] Actualizando usuario con ID: {}", id);
        if (nuevosDatos.getIdRol() == null || !rolRepository.existsById(nuevosDatos.getIdRol())) {
            throw new IllegalArgumentException("El rol con id " + nuevosDatos.getIdRol() + " no existe");
        }
        return userRepository.findById(id).map(user -> {
            user.setNombre(nuevosDatos.getNombre());
            user.setApellido(nuevosDatos.getApellido());
            user.setUsuario(nuevosDatos.getUsuario());
            user.setClave(nuevosDatos.getClave());
            user.setEmail(nuevosDatos.getEmail());
            user.setIdRol(nuevosDatos.getIdRol());
            User updated = userRepository.save(user);
            log.info("[UserService] Usuario con ID {} actualizado", id);
            return updated;
        }).orElse(null);
    }

    public boolean eliminarUsuario(Long id) {
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
