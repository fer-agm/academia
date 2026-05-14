package com.academia.inscripciones_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.academia.inscripciones_service.model.Cupos;
import com.academia.inscripciones_service.service.CuposService;
import java.util.List;

@RestController
@RequestMapping("/api/cupos")
public class CuposController {

    @Autowired
    private CuposService cuposService;

    @GetMapping("/listar")
    public List<Cupos> listar() {
        return cuposService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cupos> obtenerPorId(@PathVariable Long id) {
        Cupos cupo = cuposService.buscarPorId(id);
        return cupo != null ? ResponseEntity.ok(cupo) : ResponseEntity.notFound().build();
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<Cupos> consultarPorCurso(@PathVariable Long idCurso) {
        Cupos cupo = cuposService.obtenerPorCurso(idCurso);
        return cupo != null ? ResponseEntity.ok(cupo) : ResponseEntity.notFound().build();
    }

    @PostMapping("/crear")
    public ResponseEntity<Cupos> crear(@RequestBody Cupos cupo) {
        return ResponseEntity.ok(cuposService.guardarCupo(cupo));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Cupos> actualizar(@PathVariable Long id, @RequestBody Cupos cupo) {
        Cupos actualizado = cuposService.actualizar(id, cupo);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return cuposService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/descontar/{idCurso}")
    public ResponseEntity<String> descontar(@PathVariable Long idCurso) {
        boolean resultado = cuposService.reducirCupo(idCurso);
        return resultado ? ResponseEntity.ok("Cupo descontado exitosamente") 
                         : ResponseEntity.badRequest().body("No hay cupos disponibles");
    }
}