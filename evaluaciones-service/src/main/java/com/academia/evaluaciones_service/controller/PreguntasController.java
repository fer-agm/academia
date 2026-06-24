package com.academia.evaluaciones_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.evaluaciones_service.model.Preguntas;
import com.academia.evaluaciones_service.service.PreguntasService;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntasController {

    private final PreguntasService preguntasService;

    public PreguntasController(PreguntasService preguntasService) {
        this.preguntasService = preguntasService;
    }

    @GetMapping
    public ResponseEntity<List<Preguntas>> getAll() {
        return ResponseEntity.ok(preguntasService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Preguntas> getById(@PathVariable Long id) {
        return preguntasService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Preguntas> crear(@RequestBody Preguntas preguntas) {
        return ResponseEntity.ok(preguntasService.guardar(preguntas));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Preguntas> actualizar(@PathVariable Long id, @RequestBody Preguntas preguntas) {
        return preguntasService.getById(id)
                .map(existing -> {
                    preguntas.setIdPregunta(id);
                    return ResponseEntity.ok(preguntasService.guardar(preguntas));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        return preguntasService.getById(id)
                .map(existing -> {
                    preguntasService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}