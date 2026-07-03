package com.academia.mensajeria_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "id_mensaje")
    private Long idMensaje;

    @Column(name = "id_emisor", nullable = false)
    private String idEmisor;

    @Column(name = "id_receptor", nullable = false)
    private String idReceptor;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;
}