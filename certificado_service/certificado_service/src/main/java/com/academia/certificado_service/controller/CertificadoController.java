package com.academia.certificado_service.controller;

import com.academia.certificado_service.model.Certificado;
import com.academia.certificado_service.service.CertificadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    @Autowired
    private CertificadoService service;

    @PostMapping("/generar")
    public ResponseEntity<Certificado> crear(@RequestBody Certificado cert) {
        return ResponseEntity.ok(service.generarCertificado(cert));
    }

    @GetMapping("/estudiante/{id}")
    public ResponseEntity<List<Certificado>> buscarPorEstudiante(@PathVariable String id) {
        return ResponseEntity.ok(service.listarPorEstudiante(id));
    }
}