package br.com.almoxarifado.services;

import br.com.almoxarifado.dtos.invoice.InvoiceCreateDto;
import br.com.almoxarifado.dtos.invoice.InvoiceItemCreateDto;
import br.com.almoxarifado.dtos.invoice.InvoiceResponseDto;
import br.com.almoxarifado.entities.*;
import br.com.almoxarifado.enums.Destination;
import br.com.almoxarifado.enums.MovementType;
import br.com.almoxarifado.enums.OriginType;
import br.com.almoxarifado.exceptions.conflict.InvoiceAlreadyExistsException;
import br.com.almoxarifado.exceptions.notfound.BranchProductNotFoundException;
import br.com.almoxarifado.exceptions.notfound.InvoiceNotFoundException;
import br.com.almoxarifado.mappers.InvoiceMapper;
import br.com.almoxarifado.repositories.BranchProductRepository;
import br.com.almoxarifado.repositories.InvoiceRepository;
import br.com.almoxarifado.repositories.MovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final SupplierService supplierService;
    private final BranchService branchService;
    private final ProductService productService;
    private final BranchProductRepository branchProductRepository;
    private final InvoiceMapper invoiceMapper;
    private final MovementRepository movementRepository;

    public InvoiceService(
            InvoiceRepository invoiceRepository,
            SupplierService supplierService,
            BranchService branchService,
            ProductService productService,
            BranchProductRepository branchProductRepository,
            InvoiceMapper invoiceMapper,
            MovementRepository movementRepository) {

        this.invoiceRepository = invoiceRepository;
        this.supplierService = supplierService;
        this.branchService = branchService;
        this.productService = productService;
        this.branchProductRepository = branchProductRepository;
        this.invoiceMapper = invoiceMapper;
        this.movementRepository = movementRepository;
    }

    @Transactional
    public InvoiceResponseDto createInvoice(String cnpj, InvoiceCreateDto dto) {
        Supplier supplier = supplierService.findSupplierEntityByCnpj(cnpj);
        Branch branch = branchService.findBranchEntityByCode(dto.branchCode());
        String normalizedNumber = normalizeNumber(dto.number());
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

    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> findPendingInvoices(String branchCode) {
        if (branchCode == null || branchCode.isBlank()) {
            return invoiceRepository.findByProcessedFalseOrderByIdAsc().stream().map(invoiceMapper::toDto).toList();
        }
        Branch branch = branchService.findBranchEntityByCode(branchCode);
        return invoiceRepository.findByBranchIdAndProcessedFalseOrderByIdAsc(branch.getId()).stream().map(invoiceMapper::toDto).toList();
    }


    @Transactional
    public InvoiceResponseDto processInvoice(String cnpj, String number) {
        String normalizedNumber = normalizeNumber(number);
        Supplier supplier = supplierService.findSupplierEntityByCnpj(cnpj);

        Invoice invoice = invoiceRepository.findByNumberAndSupplierCnpj(normalizedNumber, supplier.getCnpj()).orElseThrow(() ->
                new InvoiceNotFoundException("A nota fiscal número '%s' do fornecedor '%s' não foi encontrada.".formatted(normalizedNumber, supplier.getCnpj())));

        invoice.markAsProcessed();
        for (ProductInvoice productInvoice : invoice.getProductInvoices()) {
            MovementType type = MovementType.DIRECT;
            if (productInvoice.getDestination() == Destination.STOCK) {
                BranchProduct branchProduct = branchProductRepository.findByBranchIdAndProductId(invoice.getBranch().getId(),
                        productInvoice.getProduct().getId()).orElseThrow(() ->
                        new BranchProductNotFoundException("O produto '%s' não tem vínculo na filial '%s'.".formatted(productInvoice.getProduct().getCode(),
                                invoice.getBranch().getCode())));
                branchProduct.increaseQuantity(productInvoice.getQuantity());
                type = MovementType.ENTRY;

            }
            movementRepository.save(Movement.fromInvoice(type, invoice, productInvoice.getProduct(), productInvoice.getQuantity()));
        }
        return invoiceMapper.toDto(invoice);
    }


    private String normalizeNumber(String number) {
        return number.trim();
    }
}
