package com.academia.pago_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idTransaccion;

    @NotBlank(message = "El método de pago es obligatorio")
    private String metodo;

    private LocalDateTime fecha;
}