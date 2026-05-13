package com.academia.inscripciones_service.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.academia.inscripciones_service.model.Inscripciones;
import com.academia.inscripciones_service.service.InscripcionesService;

import java.util.List;

@RestController
@RequestMapping("/inscripciones")
public class InscripcionesController {

    @Autowired
    private InscripcionesService inscripcionesService;

    @PostMapping("/inscribir")
    public Inscripciones inscribir(@RequestBody Inscripciones inscripciones) {
        return inscripcionesService.crearInscripcion(inscripciones);
    }

    @GetMapping("/estudiante/{run}")
    public List<Inscripciones> obtenerPorEstudiante(@PathVariable String run) {
        return inscripcionesService.listarPorEstudiante(run);
    }
}
