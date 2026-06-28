package com.academia.certificado_service.controller;

import com.academia.certificado_service.model.Certificado;
import com.academia.certificado_service.service.CertificadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/certificados")
@Tag(name = "Certificados", description = "Operaciones para la generación y gestión de certificados de estudiantes")
public class CertificadoController {

    private final CertificadoService certificadoService;

    // Inyección por constructor (Mismo patrón que CategoriaController)
    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    // Construye un EntityModel con el link self del certificado.
    private EntityModel<Certificado> toModel(Certificado certificado) {
        return EntityModel.of(certificado,
                linkTo(methodOn(CertificadoController.class).getById(certificado.getIdCertificado())).withSelfRel(),
                linkTo(methodOn(CertificadoController.class).getAll()).withRel("listar"));
    }

    @Operation(summary = "Listar todos los certificados",
            description = "Devuelve la lista completa de certificados registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de certificados obtenida correctamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<CollectionModel<EntityModel<Certificado>>> getAll() {
        List<EntityModel<Certificado>> certificados = certificadoService.listarTodos().stream()
                .map(this::toModel)
                .toList();
        CollectionModel<EntityModel<Certificado>> collectionModel = CollectionModel.of(certificados,
                linkTo(methodOn(CertificadoController.class).getAll()).withSelfRel());
        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Obtener un certificado por su ID",
            description = "Busca y devuelve un certificado a partir de su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificado encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe un certificado con el ID indicado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Certificado>> getById(
            @Parameter(description = "Identificador único del certificado a buscar", example = "1")
            @PathVariable Long id) {
        return certificadoService.getById(id)
                .map(this::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Listar certificados por estudiante",
            description = "Devuelve todos los certificados asociados a un estudiante determinado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de certificados del estudiante obtenida correctamente")
    })
    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<CollectionModel<EntityModel<Certificado>>> buscarPorEstudiante(
            @Parameter(description = "Identificador del estudiante cuyos certificados se desean consultar", example = "EST-2026-001")
            @PathVariable String idEstudiante) {
        List<EntityModel<Certificado>> certificados = certificadoService.listarPorEstudiante(idEstudiante).stream()
                .map(this::toModel)
                .toList();
        CollectionModel<EntityModel<Certificado>> collectionModel = CollectionModel.of(certificados,
                linkTo(methodOn(CertificadoController.class).buscarPorEstudiante(idEstudiante)).withSelfRel());
        return ResponseEntity.ok(collectionModel);
    }

    @Operation(summary = "Generar un nuevo certificado",
            description = "Crea un nuevo certificado. La fecha de emisión y el código se generan automáticamente en el servidor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificado generado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del certificado inválidos")
    })
    @PostMapping("/generar")
    public ResponseEntity<EntityModel<Certificado>> crear(
            @Valid @RequestBody Certificado certificado,
            @RequestHeader(value = org.springframework.http.HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        Certificado generado = certificadoService.generarCertificado(certificado, authHeader);
        return ResponseEntity.ok(toModel(generado));
    }

    @Operation(summary = "Actualizar un certificado existente",
            description = "Actualiza los datos de un certificado identificado por su ID. Si la fecha de emisión o el código no se envían, se conservan los valores existentes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Certificado actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos del certificado inválidos"),
            @ApiResponse(responseCode = "404", description = "No existe un certificado con el ID indicado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Certificado>> actualizar(
            @Parameter(description = "Identificador único del certificado a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody Certificado certificado,
            @RequestHeader(value = org.springframework.http.HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
        return certificadoService.getById(id)
                .map(existing -> {
                    certificado.setIdCertificado(id); // Setea el ID proveniente de la URL
                    return ResponseEntity.ok(toModel(certificadoService.generarCertificado(certificado, authHeader)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar un certificado",
            description = "Elimina un certificado a partir de su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Certificado eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No existe un certificado con el ID indicado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(
            @Parameter(description = "Identificador único del certificado a eliminar", example = "1")
            @PathVariable Long id) {
        return certificadoService.getById(id)
                .map(existing -> {
                    certificadoService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
