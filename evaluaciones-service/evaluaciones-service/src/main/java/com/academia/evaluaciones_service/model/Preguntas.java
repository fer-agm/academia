package com.academia.evaluaciones_service.model;


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

public class Preguntas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id_pregunta;
    private String enunciado;
    private int puntaje;
    private String id_examen;


}
