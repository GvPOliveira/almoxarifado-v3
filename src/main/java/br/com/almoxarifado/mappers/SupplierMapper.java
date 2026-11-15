package br.com.almoxarifado.mappers;

import br.com.almoxarifado.dtos.supplier.SupplierResponseDto;
import br.com.almoxarifado.entities.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {
    public SupplierResponseDto toDto(Supplier supplier) {
        return new SupplierResponseDto(supplier.getName(), supplier.getCnpj());
    }
}
