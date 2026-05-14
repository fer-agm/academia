package com.academia.user_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.academia.user_service.model.Rol;
import com.academia.user_service.repository.RolRepository;
import org.springframework.stereotype.Service;

@Service
public class RolService { 

    @Autowired
    private RolRepository rolRepository;

    public Rol guardarRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public List<Rol> listarTodos() {
        return rolRepository.findAll();
    }

    public Rol buscarPorId(Long id) {
        return rolRepository.findById(id).orElse(null);
    }

    public Rol buscarPorNombre(String nombre) {
        return rolRepository.findByNombreRol(nombre).orElse(null);
    }
    
    public Rol actualizarRol(Long id, Rol nuevosDatos) {
            return rolRepository.findById(id).map(rol -> {
                rol.setNombreRol(nuevosDatos.getNombreRol());
                return rolRepository.save(rol);
            }).orElse(null);
        }

        public boolean eliminarRol(Long id) {
            if (rolRepository.existsById(id)) {
                rolRepository.deleteById(id);
                return true;
            }
            return false;
        }
}

