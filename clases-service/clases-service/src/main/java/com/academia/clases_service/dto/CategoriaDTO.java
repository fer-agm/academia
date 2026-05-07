package com.academia.clases_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaDTO {
    private Long id_categoria;
    private String nombre_categoria;
    private String descripcion_categoria;

    public Categoria toModel() {
        return new Categoria(id_categoria, nombre_categoria, descripcion_categoria);
    }

    public static CategoriaDTO fromModel(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        return new CategoriaDTO(categoria.getId_categoria(), categoria.getNombre_categoria(), categoria.getDescripcion_categoria());
    }

}
