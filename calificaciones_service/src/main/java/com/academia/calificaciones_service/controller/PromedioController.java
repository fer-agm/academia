package com.academia.calificaciones_service.controller;

import com.academia.calificaciones_service.dto.PromedioDTO;
import com.academia.calificaciones_service.service.PromedioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/promedios")
public class PromedioController {

    private static final Logger log = LoggerFactory.getLogger(PromedioController.class);
    private final PromedioService promedioService;

    public PromedioController(PromedioService promedioService) {
        this.promedioService = promedioService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<PromedioDTO>> getAll() {
        log.info("Petición entrante: Listar todos los promedios");
        List<PromedioDTO> dtos = promedioService.getAll();
        
        // HATEOAS enlace autorreferencial en colecciones GET 
        dtos.forEach(dto -> dto.add(linkTo(methodOn(PromedioController.class).getById(dto.getIdPromedio())).withSelfRel()));
        
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromedioDTO> getById(@PathVariable Long id) {
        log.info("Petición entrante: Buscar promedio ID {}", id);
        return promedioService.getById(id)
                .map(dto -> {
                    dto.add(linkTo(methodOn(PromedioController.class).getAll()).withRel("lista-completa"));
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PromedioDTO> crear(@Valid @RequestBody PromedioDTO promedioDTO) {
        log.info("Petición entrante: Crear promedio");
        return ResponseEntity.ok(promedioService.guardar(promedioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromedioDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PromedioDTO promedioDTO) {
        log.info("Petición entrante: Actualizar promedio ID {}", id);
        return promedioService.getById(id)
                .map(existing -> {
                    promedioDTO.setIdPromedio(id);
                    return ResponseEntity.ok(promedioService.guardar(promedioDTO));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        log.info("Petición entrante: Eliminar promedio ID {}", id);
        return promedioService.getById(id)
                .map(existing -> {
                    promedioService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}