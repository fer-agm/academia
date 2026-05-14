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
 
    @GetMapping("/listar")
    public List<Inscripciones> listar() {
        return inscripcionesService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Inscripciones> obtenerPorId(@PathVariable Long id) {
        Inscripciones ins = inscripcionesService.buscarPorId(id);
        return ins != null ? ResponseEntity.ok(ins) : ResponseEntity.notFound().build();
    }

    @GetMapping("/estudiante/{run}")
    public ResponseEntity<List<Inscripciones>> obtenerPorEstudiante(@PathVariable String run) {
        return ResponseEntity.ok(inscripcionesService.listarPorEstudiante(run));
    }

    @PostMapping("/inscribir")
    public ResponseEntity<Inscripciones> inscribir(@RequestBody Inscripciones inscripciones) {
        return ResponseEntity.ok(inscripcionesService.crearInscripcion(inscripciones));
    }
 
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Inscripciones> actualizar(@PathVariable Long id, @RequestBody Inscripciones datos) {
        Inscripciones actualizada = inscripcionesService.actualizar(id, datos);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return inscripcionesService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}