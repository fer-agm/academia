package com.academia.pago_service.controller;

import com.academia.pago_service.model.Transaccion;
import com.academia.pago_service.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @GetMapping("/listar") // GET (Todos)
    public List<Transaccion> listar() {
        return transaccionService.listarTodas();
    }

    @GetMapping("/{id}") // GET (Por ID)
    public ResponseEntity<Transaccion> buscarPorId(@PathVariable Long id) {
        Transaccion t = transaccionService.buscarPorId(id);
        return t != null ? ResponseEntity.ok(t) : ResponseEntity.notFound().build();
    }

    @PostMapping("/generar") // POST
    public ResponseEntity<Transaccion> generar(@RequestBody Transaccion transaccion) {
        return ResponseEntity.ok(transaccionService.registrarTransaccion(transaccion));
    }

    @PutMapping("/actualizar/{id}") // PUT
    public ResponseEntity<Transaccion> actualizar(@PathVariable Long id, @RequestBody Transaccion t) {
        Transaccion actualizada = transaccionService.actualizarTransaccion(id, t);
        return actualizada != null ? ResponseEntity.ok(actualizada) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}") // DELETE
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return transaccionService.eliminar(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}