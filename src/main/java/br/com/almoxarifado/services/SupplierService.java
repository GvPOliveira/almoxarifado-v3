package br.com.almoxarifado.services;

import br.com.almoxarifado.dtos.supplier.SupplierCreateDto;
import br.com.almoxarifado.dtos.supplier.SupplierResponseDto;
import br.com.almoxarifado.entities.Supplier;
import br.com.almoxarifado.exceptions.conflict.SupplierAlreadyExistsException;
import br.com.almoxarifado.exceptions.invalid.SupplierInvalidException;
import br.com.almoxarifado.mappers.SupplierMapper;
import br.com.almoxarifado.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    public SupplierService(SupplierRepository supplierRepository, SupplierMapper supplierMapper) {
        this.supplierRepository = supplierRepository;
        this.supplierMapper = supplierMapper;
    }

    public SupplierResponseDto createSupplier(String name, String cnpj) {
        if (name == null || name.isBlank()) {
            throw new SupplierInvalidException("O nome do fornecedor é obrigatório.");
        }
        if (cnpj == null || cnpj.isBlank()) {
            throw new SupplierInvalidException("O CNPJ do fornecedor é obrigatório.");
        }

        String normalizedCnpj = normalizeCnpj(cnpj);

        if (normalizedCnpj.length() != 14) {
            throw new SupplierInvalidException("O CNPJ do fornecedor deve ter 14 caracteres.");
        }

        Optional<Supplier> foundSupplier = supplierRepository.findByCnpj(normalizedCnpj);
        if (foundSupplier.isPresent()) {
            throw new SupplierAlreadyExistsException("Já existe um fornecedor com o CNPJ '%s'.".formatted(normalizedCnpj));
        }

        String normalizedName = normalizeName(name);

        Supplier supplier = new Supplier(normalizedName, normalizedCnpj);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return supplierMapper.toDto(savedSupplier);
    }

    private String normalizeCnpj(String cnpj) {
        return cnpj.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
    }

    private String normalizeName(String name) {
        return name.trim();
    }
}
