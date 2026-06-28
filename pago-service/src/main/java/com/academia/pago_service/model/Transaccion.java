package com.academia.pago_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Schema(description = "Entidad que representa una transacción asociada a un pago")
@Entity
@Table(name = "transacciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {

    @Schema(description = "Identificador único de la transacción", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id_transaccion;

    // Una transacción pertenece a un Pago
    @Schema(description = "Pago asociado a esta transacción")
    @OneToOne
    @JoinColumn(name = "id_pago", referencedColumnName = "id_pago")
    private Pago pago;
    @Schema(description = "Método de pago utilizado", example = "TARJETA_CREDITO")
    private String metodo; // Ejemplo: "TARJETA_CREDITO", "DEBITO", "PAYPAL"
    @Schema(description = "Fecha y hora de la transacción", example = "2026-06-27T14:30:00")
    private LocalDateTime fecha;
}