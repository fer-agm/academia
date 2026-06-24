package com.academia.certificado_service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CertificadoDTO {
    private String idEstudiante;
    private Long idCurso;
    private LocalDateTime fechaEmision;
    private String codigo;
}
