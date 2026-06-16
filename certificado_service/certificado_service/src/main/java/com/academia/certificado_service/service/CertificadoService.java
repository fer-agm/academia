package com.academia.certificado_service.service;

import com.academia.certificado_service.model.Certificado;
import com.academia.certificado_service.repository.CertificadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository repository;

    public List<Certificado> listarTodos() {
        return repository.findAll();
    }

    public Optional<Certificado> getById(Long id) {
        return repository.findById(id);
    }
    
    public List<Certificado> listarPorEstudiante(String idEstudiante) {
        return repository.findByIdEstudiante(idEstudiante);
    }

    public Certificado generarCertificado(Certificado cert) {
        if (cert.getIdCertificado() == null) {
            cert.setFechaEmision(LocalDateTime.now());
            cert.setCodigo(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {
            // Si es una actualización, preservamos la fecha y el código originales de la BD
            repository.findById(cert.getIdCertificado()).ifPresent(existente -> {
                if (cert.getFechaEmision() == null) cert.setFechaEmision(existente.getFechaEmision());
                if (cert.getCodigo() == null) cert.setCodigo(existente.getCodigo());
            });
        }
        return repository.save(cert);
    }

    public void borrar(Long id) {
        repository.deleteById(id);
    }
}