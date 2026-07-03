package com.academia.notificaciones_service.dto;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa las notificaciones de certificaciones de un estudiante.")

public class NotificacionesDTO extends RepresentationModel<NotificacionesDTO> {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la notificación (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idNotificacion;

    
    @NotBlank(message = "El ID del estudiante es obligatorio")
    @Schema(description = "Identificador del estudiante asociado a la notificaión", example = "12345678-9")
    private String idEstudiante;



    @NotNull(message = "El ID del certificado es obligatorio")
    @Schema(description = "Identificador del certificado asociado a la notificación", example = "101")
    private Long idCertificado;

}