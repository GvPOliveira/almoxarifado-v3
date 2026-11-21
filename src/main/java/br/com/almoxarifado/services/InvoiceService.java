package br.com.almoxarifado.services;

import br.com.almoxarifado.dtos.invoice.InvoiceCreateDto;
import br.com.almoxarifado.dtos.invoice.InvoiceItemCreateDto;
import br.com.almoxarifado.dtos.invoice.InvoiceResponseDto;
import br.com.almoxarifado.entities.*;
import br.com.almoxarifado.enums.Destination;
import br.com.almoxarifado.exceptions.conflict.InvoiceAlreadyExistsException;
import br.com.almoxarifado.exceptions.notfound.BranchProductNotFoundException;
import br.com.almoxarifado.mappers.InvoiceMapper;
import br.com.almoxarifado.repositories.BranchProductRepository;
import br.com.almoxarifado.repositories.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SupplierService supplierService;
    private final BranchService branchService;
    private final ProductService productService;
    private final BranchProductRepository branchProductRepository;
    private final InvoiceMapper invoiceMapper;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            SupplierService supplierService,
            BranchService branchService,
            ProductService productService,
            BranchProductRepository branchProductRepository,
            InvoiceMapper invoiceMapper) {

        this.invoiceRepository = invoiceRepository;
        this.supplierService = supplierService;
        this.branchService = branchService;
        this.productService = productService;
        this.branchProductRepository = branchProductRepository;
        this.invoiceMapper = invoiceMapper;
    }

    @Transactional
    public InvoiceResponseDto createInvoice(String cnpj, InvoiceCreateDto dto) {
        Supplier supplier = supplierService.findSupplierEntityByCnpj(cnpj);
        Branch branch = branchService.findBranchEntityByCode(dto.branchCode());
        String normalizedNumber = dto.number().trim();
        Optional<Invoice> optInvoice = invoiceRepository.findByNumberAndSupplierCnpj(normalizedNumber, supplier.getCnpj());
        if (optInvoice.isPresent()) {
            throw new InvoiceAlreadyExistsException("Já existe uma nota fiscal com o número '%s' para o fornecedor '%s'.".formatted(normalizedNumber, supplier.getCnpj()));
        }
        Invoice invoice = new Invoice(normalizedNumber, supplier, branch);
        for (InvoiceItemCreateDto item : dto.items()) {
            Product product = productService.findProductEntityByCode(item.productCode());
            if (item.destination() == Destination.STOCK && !branchProductRepository.existsByBranchIdAndProductId(branch.getId(), product.getId())) {
                throw new BranchProductNotFoundException("O produto '%s' não está vinculado à filial '%s'.".formatted(product.getCode(), branch.getCode()));
            }
            invoice.addItem(product, item.quantity(), item.destination(), item.unitPrice());
        }
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return invoiceMapper.toDto(savedInvoice);
    }

}
