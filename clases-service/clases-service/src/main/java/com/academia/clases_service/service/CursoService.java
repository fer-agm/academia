package com.academia.clases_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.academia.clases_service.model.Curso;
import com.academia.clases_service.repository.CursoRepository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> getAll() {
        log.info("[CursoService] Obteniendo todos los cursos");
        return cursoRepository.findAll();
    }

    public Optional<Curso> getById(Long id) {
        log.info("[CursoService] Buscando curso con ID: {}", id);
        Optional<Curso> curso = cursoRepository.findById(id);
        if (curso.isEmpty()) {
            log.warn("[CursoService] Curso con ID {} no encontrado", id);
        }
        return curso;
    }

    public List<Curso> getByCategoria(Long idCategoria) {
        log.info("[CursoService] Buscando cursos de categoría ID: {}", idCategoria);
        return cursoRepository.findByIdCategoria(idCategoria);
    }

    public Curso guardar(Curso curso) {
        log.info("[CursoService] Guardando curso: {}", curso.getNombreCurso());
        Curso saved = cursoRepository.save(curso);
        log.info("[CursoService] Curso guardado con ID: {}", saved.getIdCurso());
        return saved;
    }

    public void borrar(Long id) {
        log.info("[CursoService] Eliminando curso con ID: {}", id);
        cursoRepository.deleteById(id);
        log.info("[CursoService] Curso con ID {} eliminado", id);
    }
}