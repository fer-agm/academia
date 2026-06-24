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

import com.academia.evaluaciones_service.model.Evaluaciones;
import com.academia.evaluaciones_service.service.EvaluacionesService;
@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionesController {

    private final EvaluacionesService evaluacionesService;  

    public EvaluacionesController(EvaluacionesService evaluacionesService) {
        this.evaluacionesService = evaluacionesService;
    }

    @GetMapping
    public ResponseEntity<List<Evaluaciones>> getAllEvaluaciones() {
        return ResponseEntity.ok(evaluacionesService.getAllEvaluaciones());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evaluaciones> getEvaluacionById(@PathVariable Long id) {
        return evaluacionesService.getEvaluacionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); 
    }

    @PostMapping
    public ResponseEntity<Evaluaciones> crearEvaluacion(@RequestBody Evaluaciones evaluaciones) {
        return ResponseEntity.ok(evaluacionesService.guardar(evaluaciones));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evaluaciones> actualizar(@PathVariable Long id, @RequestBody Evaluaciones evaluaciones) {
        return evaluacionesService.getEvaluacionById(id)
                .map(existing -> {
                    evaluaciones.setIdEvaluacion(id);
                    return ResponseEntity.ok(evaluacionesService.guardar(evaluaciones));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id){
        if (evaluacionesService.getEvaluacionById(id).isPresent()) {
            evaluacionesService.borrar(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
