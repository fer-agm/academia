package com.academia.mensajeria_service.dto;
import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa la mensajería de los usuarios.")

public class MensajeDTO extends RepresentationModel<MensajeDTO> {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único del mensaje (generado por el sistema)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idMensaje;

    
    @NotBlank(message = "El ID del emisor es obligatorio")
    @Schema(description = "Identificador del emisor asociado al mensaje", example = "10492048-9")
    private String idEmisor;



    @NotBlank(message = "El ID del receptor es obligatorio")
    @Schema(description = "Identificador del receptor asociado al mensaje", example = "9472308-6")
    private String idReceptor;


    @NotBlank(message = "El mensaje es obligatorio")
    @Schema(description = "Contenido del mensaje", example = "Eres mi postre favorito")
    private String mensaje;

}