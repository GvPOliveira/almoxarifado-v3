package br.com.almoxarifado.dtos.product;

import jakarta.validation.constraints.NotBlank;

public record ProductCreateDTO(
        @NotBlank(message = "O código do produto é obrigatório.") String code,
        @NotBlank(message = "O nome do produto é obrigatório.") String name
) {
}
