package com.academia.clases_service.dto;

import com.academia.clases_service.model.Categoria;

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
    private String nombreCategoria;
    private String descripcionCategoria;

    public Categoria toModel() {
        return new Categoria(idCategoria, nombreCategoria, descripcionCategoria);
    }

    public static CategoriaDTO fromModel(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoriaDTO(categoria.getIdCategoria(), categoria.getNombreCategoria(), categoria.getDescripcionCategoria());
    }

}
