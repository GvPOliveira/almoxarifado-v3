package br.com.almoxarifado.dtos.invoice;

import br.com.almoxarifado.enums.Destination;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record InvoiceItemCreateDto(
        @NotBlank(message = "O código do produto é obrigatório.") String productCode,
        @Positive(message = "A quantidade deve ser maior que zero.") int quantity,
        @NotNull(message = "O preço unitário é obrigatório.") @Positive(message = "O preço unitário deve ser maior que zero.") BigDecimal unitPrice,
        @NotNull(message = "O destino é obrigatório.") Destination destination) {
}
