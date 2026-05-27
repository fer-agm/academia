package com.academia.clases_service.controller;

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

import com.academia.clases_service.model.Clase;
import com.academia.clases_service.service.ClaseService;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }

    @GetMapping ("/listar")
    public ResponseEntity<List<Clase>> getAll() {
        return ResponseEntity.ok(claseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Clase> getById(@PathVariable Long id) {
        return claseService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<Clase>> getByCurso(@PathVariable Long idCurso) {
        return ResponseEntity.ok(claseService.getByCurso(idCurso));
    }

    @PostMapping
    public ResponseEntity<Clase> crear(@RequestBody Clase clase) {
        return ResponseEntity.ok(claseService.guardar(clase));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Clase> actualizar(@PathVariable Long id, @RequestBody Clase clase) {
        return claseService.getById(id)
                .map(existing -> {
                    clase.setIdClase(id);
                    return ResponseEntity.ok(claseService.guardar(clase));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        return claseService.getById(id)
                .map(existing -> {
                    claseService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}