package com.academia.certificado_service.service;

import com.academia.certificado_service.model.Certificado;
import com.academia.certificado_service.repository.CertificadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository repository;

    public Certificado generarCertificado(Certificado cert) {
        cert.setFechaEmision(LocalDateTime.now());
        cert.setCodigo(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return repository.save(cert);
    }

    public List<Certificado> listarPorEstudiante(String idEstudiante) {
        return repository.findByIdEstudiante(idEstudiante);
    }
}
