package com.academia.clases_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.academia.clases_service.model.Curso;
import com.academia.clases_service.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public ResponseEntity<List<Curso>> getAll() {
        return ResponseEntity.ok(cursoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> getById(@PathVariable Long id) {
        return cursoService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{idCategoria}")
    public ResponseEntity<List<Curso>> getByCategoria(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(cursoService.getByCategoria(idCategoria));
    }

    @PostMapping
    public ResponseEntity<Curso> crear(@RequestBody Curso curso) {
        return ResponseEntity.ok(cursoService.guardar(curso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curso> actualizar(@PathVariable Long id, @RequestBody Curso curso) {
        return cursoService.getById(id)
                .map(existing -> {
                    curso.setId_curso(id);
                    return ResponseEntity.ok(cursoService.guardar(curso));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        return cursoService.getById(id)
                .map(existing -> {
                    cursoService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}