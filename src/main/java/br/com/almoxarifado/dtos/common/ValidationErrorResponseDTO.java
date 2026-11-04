package br.com.almoxarifado.dtos.common;


import java.util.Map;

public record ValidationErrorResponseDTO(Map<String, String> errors) {
   public ValidationErrorResponseDTO {
       errors = Map.copyOf(errors);
    }
}
