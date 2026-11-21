package br.com.almoxarifado.mappers;

import br.com.almoxarifado.dtos.invoice.InvoiceItemResponseDto;
import br.com.almoxarifado.dtos.invoice.InvoiceResponseDto;
import br.com.almoxarifado.entities.Invoice;
import br.com.almoxarifado.entities.ProductInvoice;
import org.springframework.stereotype.Component;

@Component
public class InvoiceMapper {

    public InvoiceResponseDto toDto(Invoice invoice) {
        return new InvoiceResponseDto(
                invoice.getNumber(),
                invoice.getSupplier().getCnpj(),
                invoice.getBranch().getCode(),
                invoice.getTotalValue(),
                invoice.isProcessed(),
                invoice.getProductInvoices().stream().map(this::toItemDto).toList());
    }

    private InvoiceItemResponseDto toItemDto(ProductInvoice item) {
        return new InvoiceItemResponseDto(item.getProduct().getCode(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getDestination());
    }

}
