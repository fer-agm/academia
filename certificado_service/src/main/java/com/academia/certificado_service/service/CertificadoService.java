package com.academia.certificado_service.service;

import com.academia.certificado_service.model.Certificado;
import com.academia.certificado_service.repository.CertificadoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificadoService {

    private final CertificadoRepository repository;
    private final WebClient webClient;

    @Value("${api.curso.exists}")
    private String cursoExistsUrl;

    @Value("${api.usuario.exists}")
    private String usuarioExistsUrl;

    public CertificadoService(CertificadoRepository repository, WebClient webClient) {
        this.repository = repository;
        this.webClient = webClient;
    }

    public List<Certificado> listarTodos() {
        return repository.findAll();
    }

    public Optional<Certificado> getById(Long id) {
        return repository.findById(id);
    }

    public List<Certificado> listarPorEstudiante(String idEstudiante) {
        return repository.findByIdEstudiante(idEstudiante);
    }

    public Certificado generarCertificado(Certificado cert, String authHeader) {
        // Validacion cross-service: el curso debe existir (clases-service via gateway)
        Boolean existeCurso = webClient.get()
                .uri(String.format(cursoExistsUrl, cert.getIdCurso()))
                .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if (existeCurso == null) {
            throw new IllegalStateException("No se pudo validar la existencia del curso con id " + cert.getIdCurso());
        }
        if (Boolean.FALSE.equals(existeCurso)) {
            throw new IllegalArgumentException("El curso con id " + cert.getIdCurso() + " no existe");
        }

        // Validacion cross-service: el estudiante debe existir (user-service via gateway)
        Boolean existeEstudiante = webClient.get()
                .uri(String.format(usuarioExistsUrl, cert.getIdEstudiante()))
                .headers(h -> { if (authHeader != null) h.set("Authorization", authHeader); })
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
        if (existeEstudiante == null) {
            throw new IllegalStateException("No se pudo validar la existencia del estudiante con RUN " + cert.getIdEstudiante());
        }
        if (Boolean.FALSE.equals(existeEstudiante)) {
            throw new IllegalArgumentException("El estudiante con RUN " + cert.getIdEstudiante() + " no existe");
        }

        if (cert.getIdCertificado() == null) {
            cert.setFechaEmision(LocalDateTime.now());
            cert.setCodigo(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        } else {

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
