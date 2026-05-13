package com.academia.pago_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_transaccion;

    // Una transacción pertenece a un Pago
    @OneToOne
    @JoinColumn(name = "id_pago", referencedColumnName = "id_pago")
    private Pago pago; 
    private String metodo; // Ejemplo: "TARJETA_CREDITO", "DEBITO", "PAYPAL"
    private LocalDateTime fecha;
}