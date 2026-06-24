package com.academia.inscripciones_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscripciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inscripciones {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_inscripcion;

    @Column(name = "id_estudiante")
    private String idEstudiante; 

    @Column(name = "id_curso")
    private Long idCurso;

    private LocalDateTime fecha_inscripcion;
    
    private String estado; 
}