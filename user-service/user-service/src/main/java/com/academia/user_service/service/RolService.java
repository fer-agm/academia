package com.academia.user_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.academia.user_service.model.Rol;
import com.academia.user_service.repository.RolRepository;

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
}
