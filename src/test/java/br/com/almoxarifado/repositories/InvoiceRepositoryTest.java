package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.entities.Invoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class InvoiceRepositoryTest {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private BranchRepository branchRepository;


    @Test
    void findInvoiceByNumberAndSupplier() {
        Branch branch = new Branch("001", "Branch South");
        Branch branchSaved = branchRepository.save(branch);
        Invoice invoice = new Invoice("344", "Morelate", branchSaved);
        invoiceRepository.save(invoice);
        Optional<Invoice> invoiceFound = invoiceRepository.findByNumberAndSupplier("344", "Morelate");

        assertTrue(invoiceFound.isPresent());
        assertEquals("344", invoiceFound.get().getNumber());
        assertEquals("Morelate", invoiceFound.get().getSupplier());
        assertFalse(invoiceFound.get().isProcessed());
        assertFalse(invoiceFound.get().isReversed());
        assertEquals("001", invoiceFound.get().getBranch().getCode());

    }

    @Test
    void findInvoiceByNumberAndSupplierWhenNotFound() {
        Optional<Invoice> invoiceFound =
                invoiceRepository.findByNumberAndSupplier("999", "Fornecedor Inexistente");

        assertTrue(invoiceFound.isEmpty());
    }


}
