package com.academia.clases_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.clases_service.model.Categoria;
import com.academia.clases_service.repository.CategoriaRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> getAll() {
        log.info("[CategoriaService] Obteniendo todas las categorías");
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> getById(Long id) {
        log.info("[CategoriaService] Buscando categoría con ID: {}", id);
        Optional<Categoria> categoria = categoriaRepository.findById(id);
        if (categoria.isEmpty()) {
            log.warn("[CategoriaService] Categoría con ID {} no encontrada", id);
        }
        return categoria;
    }

    public Categoria guardar(Categoria categoria) {
        log.info("[CategoriaService] Guardando categoría: {}", categoria.getNombreCategoria());
        Categoria saved = categoriaRepository.save(categoria);
        log.info("[CategoriaService] Categoría guardada con ID: {}", saved.getIdCategoria());
        return saved;
    }

    public void borrar(Long id) {
        log.info("[CategoriaService] Eliminando categoría con ID: {}", id);
        categoriaRepository.deleteById(id);
        log.info("[CategoriaService] Categoría con ID {} eliminada", id);
    }
}