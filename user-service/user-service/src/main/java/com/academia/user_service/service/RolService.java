package com.academia.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.user_service.model.Rol;
import com.academia.user_service.repository.RolRepository;
import java.util.List;

@Slf4j
@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public Rol guardarRol(Rol rol) {
        log.info("[RolService] Creando rol: {}", rol.getNombreRol());
        Rol saved = rolRepository.save(rol);
        log.info("[RolService] Rol creado con ID: {}", saved.getId_rol());
        return saved;
    }

    public List<Rol> listarTodos() {
        log.info("[RolService] Obteniendo todos los roles");
        return rolRepository.findAll();
    }

    public Rol buscarPorId(Long id) {
        log.info("[RolService] Buscando rol con ID: {}", id);
        Rol rol = rolRepository.findById(id).orElse(null);
        if (rol == null) {
            log.warn("[RolService] Rol con ID {} no encontrado", id);
        }
        return rol;
    }

    public Rol actualizarRol(Long id, Rol nuevosDatos) {
        log.info("[RolService] Actualizando rol con ID: {}", id);
        return rolRepository.findById(id).map(rol -> {
            rol.setNombreRol(nuevosDatos.getNombreRol());
            Rol updated = rolRepository.save(rol);
            log.info("[RolService] Rol con ID {} actualizado", id);
            return updated;
        }).orElse(null);
    }

    public boolean eliminarRol(Long id) {
        log.info("[RolService] Eliminando rol con ID: {}", id);
        if (rolRepository.existsById(id)) {
            rolRepository.deleteById(id);
            log.info("[RolService] Rol con ID {} eliminado", id);
            return true;
        }
        log.warn("[RolService] Rol con ID {} no encontrado para eliminar", id);
        return false;
    }
}