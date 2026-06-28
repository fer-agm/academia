package com.academia.clases_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.RepresentationModel;

import com.academia.clases_service.model.Clase;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)

public class ClaseDTO extends RepresentationModel<ClaseDTO> {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la clase, generado automáticamente", example = "1")
    private Long idClase;

    @NotBlank(message = "El nombre de la clase no puede estar vacío")
    @Schema(description = "Nombre de la clase", example = "Introducción a las variables")
    private String nombreClase;

    @NotBlank(message = "El contenido no puede estar vacío")
    @Schema(description = "Contenido o temario de la clase", example = "Tipos de datos, declaración y asignación de variables")
    private String contenidoClase;

    @Positive(message = "La duración debe ser mayor a 0")
    @Schema(description = "Duración de la clase en minutos", example = "90")
    private int duracionClase;

    @Schema(description = "Indica si la clase ya fue realizada (0 = no, 1 = sí)", example = "0")
    private Long realizada;

    @NotNull(message = "El curso es obligatorio")
    @Schema(description = "Identificador del curso al que pertenece la clase", example = "1")
    private Long idCurso;

    public Clase toModel() {
        Clase cl = new Clase();
        cl.setIdClase(idClase);
        cl.setNombreClase(nombreClase);
        cl.setContenidoClase(contenidoClase);
        cl.setDuracionClase(duracionClase);
        cl.setRealizada(realizada);
        cl.setIdCurso(idCurso);
        return cl;
    }

    public static ClaseDTO fromModel(Clase cl) {
        if (cl == null) return null;
        return new ClaseDTO(cl.getIdClase(), cl.getNombreClase(), cl.getContenidoClase(), cl.getDuracionClase(), cl.getRealizada(), cl.getIdCurso());
    }
}