package br.com.almoxarifado.dtos.branchproduct;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BranchProductCreateDto(
        @NotBlank(message = "O código do produto é obrigatório.") String productCode,
        @Size(max = 15, message = "A localização deve ter no máximo 15 caracteres.") String location) {
}
