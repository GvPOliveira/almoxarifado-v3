package br.com.almoxarifado.controllers;

import br.com.almoxarifado.dtos.invoice.InvoiceCreateDto;
import br.com.almoxarifado.dtos.invoice.InvoiceResponseDto;
import br.com.almoxarifado.services.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/suppliers/{cnpj}/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponseDto createInvoice(@PathVariable String cnpj,
                                             @Valid @RequestBody InvoiceCreateDto invoiceCreateDto) {
        return invoiceService.createInvoice(cnpj, invoiceCreateDto);
    }


}

