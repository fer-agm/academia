package com.academia.pago_service.model;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;



@Schema(description = "Entidad que representa un pago de curso")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "pagos")
public class Pago {
    @Schema(description = "Identificador único del pago", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id_pago;

    @Schema(description = "RUN del estudiante que realiza el pago", example = "12345678-9")
    private String runEstudiante;

    @Schema(description = "Identificador del curso asociado al pago", example = "10")
    private Long idCurso;

    @Schema(description = "Valor neto del curso (sin IVA ni descuento)", example = "150000")
    @NotNull(message = "El valor neto es obligatorio")
    @Min(value = 100, message = "El valor mínimo es 100")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    @Column(nullable = false)
    private int valorNeto;


    @Schema(description = "IVA calculado (19% del subtotal)", example = "25650")
    @NotNull(message = "El valor del IVA es obligatorio")
    @Min(value = 0, message = "El valor mínimo es 0")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    @Column(nullable = false)
    private int iva;

    @Schema(description = "Porcentaje de descuento aplicado (0 a 100)", example = "10")
    @NotNull(message = "El % de descuento es obligatorio")
    @Min(value = 0, message = "El valor mínimo es 0")
    @Max(value = 100, message = "El valor máximo es 100")
    private int descuento;

    @Schema(description = "Total a pagar (subtotal + IVA)", example = "160650")
    @NotNull(message = "El total a pagar es obligatorio")
    @Min(value = 100, message = "El valor mínimo es 100")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    @Column(nullable = false)
    private int totalPagar;


    @Schema(description = "Medio de pago utilizado", example = "TARJETA_CREDITO")
    @NotBlank(message = "El medio de pago es obligatorio")
    @Column(nullable = false, length = 50)
    private String medioPago;

   @Schema(description = "Fecha y hora del pago", example = "2026-06-27T14:30:00")
   @Column(nullable = false)
    private LocalDateTime fecha;
}


