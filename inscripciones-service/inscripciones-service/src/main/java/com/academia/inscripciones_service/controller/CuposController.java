package com.academia.inscripciones_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.academia.inscripciones_service.model.Cupos;
import com.academia.inscripciones_service.service.CuposService;

@RestController
@RequestMapping("/cupos")
public class CuposController {

    @Autowired
    private CuposService cuposService;

    @PostMapping("/crear")
    public Cupos crear(@RequestBody Cupos cupo) {
        return cuposService.guardarCupo(cupo);
    }

    @GetMapping("/curso/{idCurso}")
    public Cupos consultar(@PathVariable Long idCurso) {
        return cuposService.obtenerPorCurso(idCurso);
    }

    @PutMapping("/descontar/{idCurso}")
    public String descontar(@PathVariable Long idCurso) {
        boolean resultado = cuposService.reducirCupo(idCurso);
        return resultado ? "Cupo descontado exitosamente" : "No hay cupos disponibles";
    }
}
