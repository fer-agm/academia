package com.academia.certificado_service.repository;

import com.academia.certificado_service.model.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {
    // Búsqueda por atributo (Requisito de búsqueda fuera de ID)
    List<Certificado> findByIdEstudiante(String idEstudiante);
}