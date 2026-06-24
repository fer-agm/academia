package com.academia.calificaciones_service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.calificaciones_service.dto.CalificacionDTO;
import com.academia.calificaciones_service.service.CalificacionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/calificaciones")
public class CalificacionController {

    private static final Logger log = LoggerFactory.getLogger(CalificacionController.class); // Trazabilidad 
    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<CalificacionDTO>> getAll() {
        log.info("Petición entrante: Listar todas las calificaciones");
        List<CalificacionDTO> dtos = calificacionService.getAll();
        
        // Aplicar HATEOAS obligatoriamente solo a métodos GET [cite: 29, 30]
        dtos.forEach(dto -> dto.add(linkTo(methodOn(CalificacionController.class).getById(dto.getIdEvaluacion())).withSelfRel()));
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalificacionDTO> getById(@PathVariable Long id) {
        log.info("Petición entrante: Buscar calificación ID {}", id);
        return calificacionService.getById(id)
                .map(dto -> {
                    dto.add(linkTo(methodOn(CalificacionController.class).getAll()).withRel("lista-completa"));
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CalificacionDTO> crear(@Valid @RequestBody CalificacionDTO calificacionDTO) { 
        log.info("Petición entrante: Crear calificación");
        return ResponseEntity.ok(calificacionService.guardar(calificacionDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalificacionDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CalificacionDTO calificacionDTO) {
        log.info("Petición entrante: Actualizar calificación ID {}", id);
        return calificacionService.getById(id)
                .map(existing -> {
                    calificacionDTO.setIdEvaluacion(id);
                    return ResponseEntity.ok(calificacionService.guardar(calificacionDTO));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.info("Petición entrante: Eliminar calificación ID {}", id);
        return calificacionService.getById(id)
                .map(existing -> {
                    calificacionService.borrar(id);
                    return ResponseEntity.noContent().<Void>build(); 
                })
                .orElse(ResponseEntity.notFound().build());
    }
}