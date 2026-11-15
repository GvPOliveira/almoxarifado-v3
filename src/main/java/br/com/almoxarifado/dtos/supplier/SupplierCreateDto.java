package br.com.almoxarifado.dtos.supplier;

import jakarta.validation.constraints.NotBlank;

public record SupplierCreateDto(@NotBlank(message = "O nome é obrigatório.") String name,
                                @NotBlank(message = "O CNPJ é obrigatório.") String cnpj
) {
}
