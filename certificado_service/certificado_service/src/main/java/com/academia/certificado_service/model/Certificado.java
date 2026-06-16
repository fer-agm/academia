package com.academia.certificado_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificado")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCertificado;

    @Column(nullable = false)
    private String idEstudiante;

    @Column(nullable = false)
    private Long idCurso;

    private LocalDateTime fechaEmision;

    @Column(unique = true, nullable = false)
    private String codigo;
}
