package br.com.almoxarifado.controllers;

import br.com.almoxarifado.dtos.invoice.InvoiceCreateDto;
import br.com.almoxarifado.dtos.invoice.InvoiceResponseDto;
import br.com.almoxarifado.services.InvoiceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/suppliers/{cnpj}/invoices")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceResponseDto createInvoice(@PathVariable String cnpj,
                                            @Valid @RequestBody InvoiceCreateDto invoiceCreateDto) {
        return invoiceService.createInvoice(cnpj, invoiceCreateDto);
    }

    @PostMapping("/suppliers/{cnpj}/invoices/{number}/process")
    public InvoiceResponseDto processInvoice(@PathVariable String cnpj, @PathVariable String number) {
        return invoiceService.processInvoice(cnpj, number);
    }

    @GetMapping("/invoices/pending")
    public List<InvoiceResponseDto> findPendingInvoices(@RequestParam(required = false) String branchCode) {
        return invoiceService.findPendingInvoices(branchCode);
    }
}

