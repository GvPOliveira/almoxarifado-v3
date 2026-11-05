package br.com.almoxarifado.dtos.branch;

import jakarta.validation.constraints.NotBlank;

public record BranchCreateDto(
        @NotBlank(message = "O código da filial é obrigatório.") String code,
        @NotBlank(message = "O nome da filial é obrigatório.") String name
) {
}
