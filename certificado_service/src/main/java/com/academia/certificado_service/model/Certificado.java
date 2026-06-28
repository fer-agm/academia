package com.academia.certificado_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único del certificado, generado automáticamente por el sistema.", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idCertificado;

    @NotBlank(message = "El idEstudiante es obligatorio")
    @Column(nullable = false)
    @Schema(description = "Identificador del estudiante al que pertenece el certificado.", example = "EST-2026-001")
    private String idEstudiante;

    @NotNull(message = "El idCurso es obligatorio")
    @Column(nullable = false)
    @Schema(description = "Identificador del curso asociado al certificado.", example = "101")
    private Long idCurso;

    @Schema(description = "Fecha y hora de emisión del certificado. Generada automáticamente por el servidor al crear el certificado.", example = "2026-06-27T14:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime fechaEmision;

    @Column(unique = true, nullable = false)
    @Schema(description = "Código único del certificado. Generado automáticamente por el servidor.", example = "A1B2C3D4", accessMode = Schema.AccessMode.READ_ONLY)
    private String codigo;
}
