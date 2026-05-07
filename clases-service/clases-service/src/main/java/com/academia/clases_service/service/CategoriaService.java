package com.academia.clases_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.academia.clases_service.model.Categoria;
import com.academia.clases_service.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> getAll() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> getById(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria guardar(Categoria categoria) {
        return categoriaRepository.save(categoria);
    }

    public void borrar(Long id) {
        categoriaRepository.deleteById(id);
    }
}