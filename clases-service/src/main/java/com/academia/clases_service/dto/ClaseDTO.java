package com.academia.clases_service.dto;

import org.springframework.hateoas.RepresentationModel;

import com.academia.clases_service.model.Clase;

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
    private Long idClase;

    @NotBlank(message = "El nombre de la clase no puede estar vacío")
    private String nombreClase;

    @NotBlank(message = "El contenido no puede estar vacío")
    private String contenidoClase;

    @Positive(message = "La duración debe ser mayor a 0")
    private int duracionClase;

    private Long realizada;

    @NotNull(message = "El curso es obligatorio")
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