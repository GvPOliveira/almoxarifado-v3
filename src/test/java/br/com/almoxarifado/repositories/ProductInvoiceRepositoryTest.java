package br.com.almoxarifado.repositories;


import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.entities.Invoice;
import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.entities.ProductInvoice;
import br.com.almoxarifado.enums.Destination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ProductInvoiceRepositoryTest {

    @Autowired
    private ProductInvoiceRepository productInvoiceRepository;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BranchRepository branchRepository;


    @Test
    void foundAllProductInvoiceByInvoiceId() {
        Product product1 = new Product("P001", "Parafuso 1/2");
        Product product2 = new Product("P002", "Parafuso 3/4");
        Product product3 = new Product("P003", "Parafuso 5/8");
        Product savedProduct1 = productRepository.save(product1);
        Product savedProduct2 = productRepository.save(product2);
        Product savedProduct3 = productRepository.save(product3);
        Branch branch = new Branch("001", "Branch South");
        Branch savedBranch = branchRepository.save(branch);
        Invoice invoice = new Invoice("566", "Morelate", savedBranch);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        ProductInvoice productInvoice1 = new ProductInvoice(savedInvoice, savedProduct1, 100, Destination.STOCK);
        ProductInvoice productInvoice2 = new ProductInvoice(savedInvoice, savedProduct2, 200, Destination.STOCK);
        ProductInvoice productInvoice3 = new ProductInvoice(savedInvoice, savedProduct3, 300, Destination.DIRECT);
        List<ProductInvoice> productInvoiceList = new ArrayList<ProductInvoice>();
        productInvoiceList.add(productInvoice1);
        productInvoiceList.add(productInvoice2);
        productInvoiceList.add(productInvoice3);
        productInvoiceRepository.saveAll(productInvoiceList);

        List<ProductInvoice> foundListProductInvoice = productInvoiceRepository.findByInvoiceId(savedInvoice.getId());

        assertFalse(foundListProductInvoice.isEmpty());
        assertEquals(3, foundListProductInvoice.size());
        assertEquals(Destination.STOCK, foundListProductInvoice.get(0).getDestination());
        assertEquals(Destination.STOCK, foundListProductInvoice.get(1).getDestination());
        assertEquals(Destination.DIRECT, foundListProductInvoice.get(2).getDestination());
        assertEquals("566", foundListProductInvoice.get(0).getInvoice().getNumber());
        assertEquals("P003", foundListProductInvoice.get(2).getProduct().getCode());
    }

    @Test
    void foundListProductInvoiceNotFound(){
        Branch branch = new Branch("002","Branch South");
        Branch savedBranch = branchRepository.save(branch);
        Invoice invoice = new Invoice("5000", "Morelate",savedBranch);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        List<ProductInvoice> productInvoiceList = productInvoiceRepository.findByInvoiceId(savedInvoice.getId());

        assertTrue(productInvoiceList.isEmpty());
    }


}
