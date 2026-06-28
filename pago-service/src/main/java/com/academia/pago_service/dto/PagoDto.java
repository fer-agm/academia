package com.academia.pago_service.dto;

import java.time.LocalDateTime;

import com.academia.pago_service.model.Pago;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Datos de un pago de curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDto {

    @Schema(description = "Identificador único del pago (generado automáticamente)", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id_pago;

    @Schema(description = "RUN del estudiante que realiza el pago", example = "12345678-9")
    @NotBlank(message = "El RUN del estudiante es obligatorio")
    private String runEstudiante;

    @Schema(description = "Identificador del curso asociado al pago", example = "10")
    @NotNull(message = "El ID de curso es obligatorio")
    private Long idCurso;

    @Schema(description = "Valor neto del curso (sin IVA ni descuento)", example = "150000")
    @NotNull(message = "El valor neto es obligatorio")
    @Min(value = 100, message = "El valor mínimo es 100")
    @Max(value = 1000000, message = "El valor máximo es 1000000")
    private int valorNeto;

    @Schema(description = "Porcentaje de descuento aplicado (0 a 100)", example = "10")
    @NotNull(message = "El % de descuento es obligatorio")
    @Min(value = 0, message = "El valor mínimo es 0")
    @Max(value = 100, message = "El valor máximo es 100")
    private int descuento;

    @Schema(description = "IVA calculado automáticamente (19% del subtotal)", example = "25650",
            accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int iva;

    @Schema(description = "Total a pagar calculado automáticamente (subtotal + IVA)", example = "160650",
            accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int totalPagar;

    @Schema(description = "Medio de pago utilizado", example = "TARJETA_CREDITO")
    @NotBlank(message = "El medio de pago es obligatorio")
    private String medioPago;

    @Schema(description = "Fecha y hora del pago (asignada automáticamente)", example = "2026-06-27T14:30:00",
            accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime fecha;

    public Pago toModel() {
        return new Pago(
                id_pago,
                runEstudiante,
                idCurso,
                valorNeto,
                iva ,
                descuento,
                totalPagar,
                medioPago,
                fecha
        );
    }

    public static PagoDto fromModel(Pago pago) {
        return new PagoDto(
                pago.getId_pago(),
                pago.getRunEstudiante(),
                pago.getIdCurso(),
                pago.getValorNeto(),
                pago.getDescuento(),
                pago.getIva(),
                pago.getTotalPagar(),
                pago.getMedioPago(),
                pago.getFecha()
        );
    }
}
//idpago run idcurso valorneto iva descuento totalpagar mediopago fecha




