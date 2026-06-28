package com.academia.clases_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

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

public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)

    private Long idCurso;
    private String nombreCurso;
    private int duracionCurso;
    private String descripcionCurso;
    private double valorCurso;
    private Long idCategoria;
    private int maxCupos;
    //private Long idInstructor;




}
