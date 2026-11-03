package br.com.almoxarifado.dtos.product;

import jakarta.validation.constraints.NotBlank;

public record ProductCreateDTO(@NotBlank String code, @NotBlank String name) {
}