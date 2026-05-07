package com.academia.clases_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.academia.clases_service.model.Clase;
import com.academia.clases_service.repository.ClaseRepository;

@Service
public class ClaseService {

    private final ClaseRepository claseRepository;

    public ClaseService(ClaseRepository claseRepository) {
        this.claseRepository = claseRepository;
    }

    public List<Clase> getAll() {
        return claseRepository.findAll();
    }

    public Optional<Clase> getById(Long id) {
        return claseRepository.findById(id);
    }

    public List<Clase> getByCurso(Long idCurso) {
        return claseRepository.findByIdCurso(idCurso);
    }

    public Clase guardar(Clase clase) {
        return claseRepository.save(clase);
    }

    public void borrar(Long id) {
        claseRepository.deleteById(id);
    }
}