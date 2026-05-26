package com.academia.clases_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Clase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idClase;

    private String nombreClase;
    private String contenidoClase;
    private int duracionClase;

    private Long realizada;
    private Long idCurso;

}


