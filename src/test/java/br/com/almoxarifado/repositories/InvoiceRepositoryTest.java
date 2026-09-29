package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.entities.Invoice;
import br.com.almoxarifado.entities.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private SupplierRepository supplierRepository;


    @Test
    void findInvoiceByNumberAndSupplierCnpj() {
        Branch branch = new Branch("001", "Branch South");
        Branch branchSaved = branchRepository.save(branch);
        Supplier supplier = new Supplier("Fornecedor 1", "05.368.025/0001-56");
        Supplier savedSupplier = supplierRepository.save(supplier);

        Invoice invoice = new Invoice("344", savedSupplier, branchSaved, new BigDecimal("10.000"));
        invoiceRepository.save(invoice);
        Optional<Invoice> invoiceFound = invoiceRepository.findByNumberAndSupplierCnpj("344", supplier.getCnpj());

        assertTrue(invoiceFound.isPresent());
        assertEquals("344", invoiceFound.get().getNumber());
        assertEquals("Fornecedor 1", invoiceFound.get().getSupplier().getName());
        assertFalse(invoiceFound.get().isProcessed());
        assertFalse(invoiceFound.get().isReversed());
        assertEquals("001", invoiceFound.get().getBranch().getCode());

    }

    @Test
    void findInvoiceByNumberAndSupplierWhenNotFound() {
        Supplier supplier = new Supplier("Fornecedor 1", "05.053.003/0002-56");
        Optional<Invoice> invoiceFound =
                invoiceRepository.findByNumberAndSupplierCnpj("999", supplier.getCnpj());

        assertTrue(invoiceFound.isEmpty());
    }


}
