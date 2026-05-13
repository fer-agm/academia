package com.academia.pago_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.pago_service.model.Pago;
import com.academia.pago_service.service.PagoService;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/registrar")
    public Pago crear(@RequestBody Pago pago) {
        return pagoService.registrarPago(pago);
    }

    @GetMapping("/historial/{run}")
    public List<Pago> historial(@PathVariable String run) {
        return pagoService.historialPorAlumno(run);
    }


}
