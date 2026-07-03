package com.academia.mensajeria_service.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academia.mensajeria_service.dto.MensajeDTO;
import com.academia.mensajeria_service.service.MensajeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mensajeria")
@Tag(name = "Mensajes", description = "Operaciones CRUD sobre los mensajes")
public class MensajeController {

    private static final Logger log = LoggerFactory.getLogger(MensajeController.class);
    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @Operation(summary = "Listar todos los mensajes",
            description = "Devuelve la lista completa de mensajes, cada uno con su enlace HATEOAS autorreferencial.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de mensajes obtenido correctamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<List<MensajeDTO>> getAll() {
        log.info("Petición entrante: Listar todas los mensajes");
        List<MensajeDTO> dtos = mensajeService.getAll();
        
        // HATEOAS enlace autorreferencial en colecciones GET 
        dtos.forEach(dto -> dto.add(linkTo(methodOn(MensajeController.class).getById(dto.getIdMensaje())).withSelfRel()));
        
        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "Obtener un mensaje por ID",
            description = "Busca y devuelve un mensaje específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensaje encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un mensaje con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MensajeDTO> getById(
            @Parameter(description = "Identificador único del mensaje", example = "1") @PathVariable Long id) {
        log.info("Petición entrante: Buscar mensaje ID {}", id);
        return mensajeService.getById(id)
                .map(dto -> {
                    dto.add(linkTo(methodOn(MensajeController.class).getAll()).withRel("lista-completa"));
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Crear un mensaje",
            description = "Registra un nuevo mensaje a partir de los datos enviados en el cuerpo de la petición.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mensaje creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    @PostMapping
    public ResponseEntity<MensajeDTO> generarMensaje(@Valid @RequestBody MensajeDTO mensajeDTO,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        log.info("Petición entrante: Crear mensaje");
        return ResponseEntity.ok(mensajeService.generarMensaje(mensajeDTO, authHeader));
    }

//     @Operation(summary = "Actualizar un mensaje",
//             description = "Actualiza los datos de un mensaje existente identificado por su ID.")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "200", description = "Mensaje actualizado correctamente"),
//             @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
//             @ApiResponse(responseCode = "404", description = "No existe un mensaje con el ID indicado")
//     })
//     @PutMapping("/{id}")
//     public ResponseEntity<MensajeDTO> actualizar(
//             @Parameter(description = "Identificador único del mensaje a actualizar", example = "1") @PathVariable Long id,
//             @Valid @RequestBody MensajeDTO mensajeDTO,
//             @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
//         log.info("Petición entrante: Actualizar mensaje ID {}", id);
//         return mensajeService.getById(id)
//                 .map(existing -> {
//                     mensajeDTO.setIdMensaje(id);
//                     return ResponseEntity.ok(mensajeService.guardar(mensajeDTO, authHeader));
//                 })
//                 .orElse(ResponseEntity.notFound().build());
//     }

//     @Operation(summary = "Eliminar un mensaje",
//             description = "Elimina un mensaje existente identificado por su ID.")
//     @ApiResponses(value = {
//             @ApiResponse(responseCode = "204", description = "Mensaje eliminado correctamente"),
//             @ApiResponse(responseCode = "404", description = "No existe un mensaje con el ID indicado")
//     })
//     @DeleteMapping("/{id}")
//     public ResponseEntity<Void> borrar(
//             @Parameter(description = "Identificador único del mensaje a eliminar", example = "1") @PathVariable Long id) {
//         log.info("Petición entrante: Eliminar mensaje ID {}", id);
//         return mensajeService.getById(id)
//                 .map(existing -> {
//                     mensajeService.borrar(id);
//                     return ResponseEntity.noContent().<Void>build();
//                 })
//                 .orElse(ResponseEntity.notFound().build());
//     }
}