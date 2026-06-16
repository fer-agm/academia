package com.academia.certificado_service.controller;

import com.academia.certificado_service.model.Certificado;
import com.academia.certificado_service.service.CertificadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    private final CertificadoService certificadoService;

    // Inyección por constructor (Mismo patrón que CategoriaController)
    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Certificado>> getAll() {
        return ResponseEntity.ok(certificadoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificado> getById(@PathVariable Long id) {
        return certificadoService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<Certificado>> buscarPorEstudiante(@PathVariable String idEstudiante) {
        return ResponseEntity.ok(certificadoService.listarPorEstudiante(idEstudiante));
    }

    @PostMapping("/generar")
    public ResponseEntity<Certificado> crear(@RequestBody Certificado certificado) {
        return ResponseEntity.ok(certificadoService.generarCertificado(certificado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Certificado> actualizar(@PathVariable Long id, @RequestBody Certificado certificado) {
        return certificadoService.getById(id)
                .map(existing -> {
                    certificado.setIdCertificado(id); // Setea el ID proveniente de la URL
                    return ResponseEntity.ok(certificadoService.generarCertificado(certificado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable Long id) {
        return certificadoService.getById(id)
                .map(existing -> {
                    certificadoService.borrar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}