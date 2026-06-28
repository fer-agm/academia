package com.academia.clases_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.academia.clases_service.model.Categoria;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Identificador único de la categoría, generado automáticamente", example = "1")
    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    @Schema(description = "Nombre de la categoría", example = "Programación")
    private String nombreCategoria;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    @Schema(description = "Descripción de la categoría", example = "Cursos de programación y desarrollo de software")
    private String descripcionCategoria;

    public Categoria toModel() {
        Categoria c = new Categoria();
        c.setIdCategoria(idCategoria);
        c.setNombreCategoria(nombreCategoria);
        c.setDescripcionCategoria(descripcionCategoria);
        return c;
    }

    public static CategoriaDTO fromModel(Categoria c) {
        if (c == null) return null;
        return new CategoriaDTO(c.getIdCategoria(), c.getNombreCategoria(), c.getDescripcionCategoria());
    }
}