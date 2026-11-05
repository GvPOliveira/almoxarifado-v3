package br.com.almoxarifado.dtos.common;


import java.util.Map;

public record ValidationErrorResponseDto(Map<String, String> errors) {
   public ValidationErrorResponseDto {
       errors = Map.copyOf(errors);
    }
}
