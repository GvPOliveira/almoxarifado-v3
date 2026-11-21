package br.com.almoxarifado.dtos.invoice;

import java.math.BigDecimal;
import java.util.List;

public record InvoiceResponseDto(
        String number,
        String supplierCnpj,
        String branchCode,
        BigDecimal totalValue,
        boolean processed,
        List<InvoiceItemResponseDto> items
) {
    public InvoiceResponseDto {
        items = List.copyOf(items);
    }
}
