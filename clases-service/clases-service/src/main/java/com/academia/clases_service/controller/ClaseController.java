package com.academia.clases_service.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
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

import com.academia.clases_service.dto.ClaseDTO;
import com.academia.clases_service.model.Clase;
import com.academia.clases_service.service.ClaseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clases")
public class ClaseController {

    private final ClaseService claseService;

    public ClaseController(ClaseService claseService) {
        this.claseService = claseService;
    }

    @GetMapping ("/listar")
    public ResponseEntity<CollectionModel<ClaseDTO>> getAll() {
        List<ClaseDTO> clasesDto = claseService.getAll().stream()
        .map(ClaseDTO::fromModel)
        .map(this::agregarLinks)
        .collect(Collectors.toList());

        CollectionModel<ClaseDTO> collectionModel = CollectionModel.of(clasesDto);
        collectionModel.add(linkTo(methodOn(ClaseController.class).getAll()).withSelfRel());
        
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaseDTO> getById(@PathVariable Long id) {
        return claseService.getById(id)
                .map(ClaseDTO::fromModel)
                .map(this::agregarLinks)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<CollectionModel<ClaseDTO>> getByCurso(@PathVariable Long idCurso) {
        List<ClaseDTO> clasesDto = claseService.getByCurso(idCurso).stream()
                .map(ClaseDTO::fromModel)
                .map(this::agregarLinks)
                .collect(Collectors.toList());

        CollectionModel<ClaseDTO> collectionModel = CollectionModel.of(clasesDto);
        collectionModel.add(linkTo(methodOn(ClaseController.class).getByCurso(idCurso)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
    @PostMapping
    public ResponseEntity<ClaseDTO> crear(@Valid @RequestBody ClaseDTO claseDto) {
        Clase guardada = claseService.guardar(claseDto.toModel());
        ClaseDTO responseDto = agregarLinks(ClaseDTO.fromModel(guardada));
        
        return ResponseEntity
            .created(responseDto.getRequiredLink("self").toUri())
            .body(responseDto);
        
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClaseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody ClaseDTO claseDto) {
        return claseService.getById(id)
                .map(existing -> {
                    claseDto.setIdClase(id);
                    Clase actualizada = claseService.guardar(claseDto.toModel());
                    ClaseDTO responseDto = agregarLinks(ClaseDTO.fromModel(actualizada));
                    return ResponseEntity.ok(responseDto);

                })
                .orElse(ResponseEntity.notFound().build());
            }
                    
                    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        return claseService.getById(id)
                .map(existing -> {
                    claseService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private ClaseDTO agregarLinks(ClaseDTO dto) {
        dto.add(linkTo(methodOn(ClaseController.class).getById(dto.getIdClase())).withSelfRel());
        dto.add(linkTo(methodOn(ClaseController.class).getAll()).withRel("clases"));
        return dto;
    }


}