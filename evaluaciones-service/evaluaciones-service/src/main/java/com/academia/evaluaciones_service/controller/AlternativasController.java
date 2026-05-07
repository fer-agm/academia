package com.academia.evaluaciones_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.academia.evaluaciones_service.model.Alternativas;
import com.academia.evaluaciones_service.service.AlternativasService;

import java.util.List;

@RestController
@RequestMapping("/api/alternativas")
public class AlternativasController {

    private final AlternativasService alternativasService;

    public AlternativasController(AlternativasService alternativasService) {
        this.alternativasService = alternativasService;
    }

    @GetMapping
    public ResponseEntity<List<Alternativas>> getAll() {
        return ResponseEntity.ok(alternativasService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alternativas> getById(@PathVariable Long id) {
        return alternativasService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Alternativas> crear(@RequestBody Alternativas alternativas) {
        return ResponseEntity.ok(alternativasService.guardar(alternativas));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alternativas> actualizar(@PathVariable Long id, @RequestBody Alternativas alternativas) {
        return alternativasService.getById(id)
                .map(existing -> {
                    alternativas.setId_alternativa(id);
                    return ResponseEntity.ok(alternativasService.guardar(alternativas));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        return alternativasService.getById(id)
                .map(existing -> {
                    alternativasService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}