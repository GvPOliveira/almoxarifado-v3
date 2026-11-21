package br.com.almoxarifado.dtos.invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record InvoiceCreateDto(
        @NotBlank(message = "O número da nota é obrigatório.") String number,
        @NotBlank(message = "O código da filial é obrigatório.") String branchCode,
        @NotEmpty(message = "A nota deve ter pelo menos um item.") @Valid List<InvoiceItemCreateDto> items) {
}
