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

public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id_curso;
    private String nombre_curso;
    private int duracion_curso;
    private String descripcion_curso;
    private double valor_curso;
    private Long id_categoria;
    private int max_cupos;
    //private Long id_instructor;




}
