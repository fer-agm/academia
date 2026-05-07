package com.academia.clases_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.academia.clases_service.model.Curso;
import com.academia.clases_service.repository.CursoRepository;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> getAll() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> getById(Long id) {
        return cursoRepository.findById(id);
    }

    public List<Curso> getByCategoria(Long idCategoria) {
        return cursoRepository.findByIdCategoria(idCategoria);
    }

    public Curso guardar(Curso curso) {
        return cursoRepository.save(curso);
    }

    public void borrar(Long id) {
        cursoRepository.deleteById(id);
    }
}