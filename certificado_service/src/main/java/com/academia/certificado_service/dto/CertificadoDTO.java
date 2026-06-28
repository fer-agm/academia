package com.academia.certificado_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CertificadoDTO {

    @Schema(description = "Identificador del estudiante al que pertenece el certificado.", example = "EST-2026-001")
    private String idEstudiante;

    @Schema(description = "Identificador del curso asociado al certificado.", example = "101")
    private Long idCurso;

    @Schema(description = "Fecha y hora de emisión del certificado.", example = "2026-06-27T14:30:00")
    private LocalDateTime fechaEmision;

    @Schema(description = "Código único del certificado.", example = "A1B2C3D4")
    private String codigo;
}
