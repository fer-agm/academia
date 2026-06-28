package com.academia.pago_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Schema(description = "Datos de una transacción de pago")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDto {
    @Schema(description = "Identificador único de la transacción (generado automáticamente)", example = "1",
            accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idTransaccion;

    @Schema(description = "Método de pago utilizado", example = "TARJETA_CREDITO")
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodo;

    @Schema(description = "Fecha y hora de la transacción", example = "2026-06-27T14:30:00")
    private LocalDateTime fecha;
}