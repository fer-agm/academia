package com.academia.inscripciones_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cupos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cupos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cupo; // Generalmente se añade una PK autoincremental

    private Long id_curso; // Referencia al curso
    
    private Integer num_maximo;
    private Integer num_disponible;
}