package com.academia.inscripciones_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.academia.inscripciones_service.model.Inscripciones;
import com.academia.inscripciones_service.service.InscripcionesService;
import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionesController {

    @Autowired
    private InscripcionesService inscripcionesService;

    // 1. GET - Listar todos 
    @GetMapping("/listar")
    public List<Inscripciones> listar() {
        return inscripcionesService.listarTodas();
    }

    // 2. GET - Por ID 
    @GetMapping("/{id}")
    public ResponseEntity<Inscripciones> obtenerPorId(@PathVariable Long id) {
        Inscripciones ins = inscripcionesService.buscarPorId(id);
        return ins != null ? ResponseEntity.ok(ins) : ResponseEntity.notFound().build();
    }

    // 3. GET - Por Atributo (RUN) 
    @GetMapping("/estudiante/{run}")
    public ResponseEntity<List<Inscripciones>> obtenerPorEstudiante(@PathVariable String run) {
        return ResponseEntity.ok(inscripcionesService.listarPorEstudiante(run));
    }

    // 4. POST - Inscribir 
    @PostMapping("/inscribir")
    public ResponseEntity<Inscripciones> inscribir(@RequestBody Inscripciones inscripciones) {
        return ResponseEntity.ok(inscripcionesService.crearInscripcion(inscripciones));
    }

    // 5. PUT - Actualizar 
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Inscripciones> actualizar(@PathVariable Long id, @RequestBody Inscripciones datos) {
        Inscripciones actualizada = inscripcionesService.actualizar(id, datos);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    // 6. DELETE - Eliminar 
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return inscripcionesService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}