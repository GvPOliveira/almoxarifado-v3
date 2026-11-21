package br.com.almoxarifado.dtos.invoice;

import br.com.almoxarifado.enums.Destination;

import java.math.BigDecimal;

public record InvoiceItemResponseDto(
        String productCode,
        int quantity,
        BigDecimal unitPrice,
        Destination destination
) {

}