package com.academia.user_service.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long idRol;
    private String nombreRol;
}