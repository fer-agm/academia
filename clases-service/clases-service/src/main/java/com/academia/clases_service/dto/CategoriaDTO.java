package com.academia.clases_service.dto;

import com.academia.clases_service.model.Categoria;
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
    private Long idCategoria;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombreCategoria;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcionCategoria;

    public Categoria toModel() {
        Categoria c = new Categoria();
        c.setNombreCategoria(nombreCategoria);
        c.setDescripcionCategoria(descripcionCategoria);
        return c;
    }

    public static CategoriaDTO fromModel(Categoria c) {
        if (c == null) return null;
        return new CategoriaDTO(c.getIdCategoria(), c.getNombreCategoria(), c.getDescripcionCategoria());
    }
}