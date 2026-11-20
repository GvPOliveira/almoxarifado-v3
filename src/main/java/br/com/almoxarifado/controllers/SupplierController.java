package br.com.almoxarifado.controllers;

import br.com.almoxarifado.dtos.supplier.SupplierCreateDto;
import br.com.almoxarifado.dtos.supplier.SupplierResponseDto;
import br.com.almoxarifado.services.SupplierService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierResponseDto createSupplier(@Valid @RequestBody SupplierCreateDto supplierCreateDto) {
        return supplierService.createSupplier(supplierCreateDto.name(), supplierCreateDto.cnpj());
    }

    @GetMapping("/{cnpj}")
    public SupplierResponseDto findSupplierByCnpj(@PathVariable String cnpj) {
        return supplierService.findSupplierByCnpj(cnpj);
    }
}
