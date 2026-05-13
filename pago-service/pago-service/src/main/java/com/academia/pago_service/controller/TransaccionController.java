package com.academia.pago_service.controller;

import com.academia.pago_service.model.Transaccion;
import com.academia.pago_service.service.TransaccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @PostMapping("/generar")
    public Transaccion generar(@RequestBody Transaccion transaccion) {
        return transaccionService.registrarTransaccion(transaccion);
    }
}
