package com.academia.pago_service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.academia.pago_service.model.Pago;
import com.academia.pago_service.service.PagoService;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/listar")
    public List<Pago> listar() {
        return pagoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pago> buscar(@PathVariable Long id) {
        Pago pago = pagoService.buscarPorId(id);
        return pago != null ? ResponseEntity.ok(pago) : ResponseEntity.notFound().build();
    }

    @GetMapping("/historial/{run}")
    public ResponseEntity<List<Pago>> historial(@PathVariable String run) {
        return ResponseEntity.ok(pagoService.historialPorAlumno(run));
    }

    @PostMapping("/registrar")
    public ResponseEntity<Pago> crear(@RequestBody Pago pago) {
        return ResponseEntity.ok(pagoService.registrarPago(pago));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Pago> actualizar(@PathVariable Long id, @RequestBody Pago pago) {
        Pago actualizado = pagoService.actualizarPago(id, pago);
        return actualizado != null ? ResponseEntity.ok(actualizado) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return pagoService.eliminarPago(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}