package br.com.almoxarifado.entities;

import br.com.almoxarifado.enums.Destination;
import br.com.almoxarifado.exceptions.conflict.InvoiceAlreadyProcessedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class InvoiceTest {

    @Test
    void shouldCreateInvoiceWhenDataIsValid() {
        Supplier supplier = new Supplier("Morelate", "02703098000114");
        Branch branch = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("001", supplier, branch);
        Product product = new Product("00333", "Parafuso 3x4");
        invoice.addItem(product, 2, Destination.STOCK, new BigDecimal("10.50"));
        invoice.addItem(product, 3, Destination.STOCK, new BigDecimal("5.00"));

        assertEquals(2, invoice.getProductInvoices().size());
        assertEquals(new BigDecimal("36.00"), invoice.getTotalValue());
    }

    @Test
    void shouldMarkInvoiceAsProcessed() {
        Supplier supplier = new Supplier("Morelate", "02703098000114");
        Branch branch = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("001", supplier, branch);
        invoice.markAsProcessed();

        assertTrue(invoice.isProcessed());
    }

    @Test
    void shouldThrowAlreadyProcessedWhenInvoiceIsProcessedTwice() {
        Supplier supplier = new Supplier("Morelate", "02703098000114");
        Branch branch = new Branch("001", "Branch South");
        Invoice invoice = new Invoice("001", supplier, branch);
        invoice.markAsProcessed();

        assertThrows(InvoiceAlreadyProcessedException.class, invoice::markAsProcessed);
    }
}
