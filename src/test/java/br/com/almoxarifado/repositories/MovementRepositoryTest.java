package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.entities.Invoice;
import br.com.almoxarifado.entities.Movement;
import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.entities.Supplier;
import br.com.almoxarifado.enums.MovementType;
import br.com.almoxarifado.enums.OriginType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class MovementRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private MovementRepository movementRepository;

    private Product savedProduct;
    private Branch savedBranch;
    private Invoice savedInvoice;

    @BeforeEach
    void setUp() {
        savedProduct = productRepository.save(new Product("P030", "Teclado Mecanico"));
        savedBranch = branchRepository.save(new Branch("002", "Branch West"));
        Supplier savedSupplier = supplierRepository.save(new Supplier("Fornecedor 1", "05368025000156"));
        savedInvoice = invoiceRepository.save(new Invoice("100", savedSupplier, savedBranch));
    }

    @Test
    void shouldReturnMovementsWhenBranchAndProductHaveMovements() {
        movementRepository.saveAll(List.of(
                Movement.fromInvoice(MovementType.ENTRY, savedInvoice, savedProduct, 200),
                Movement.fromInvoice(MovementType.DIRECT, savedInvoice, savedProduct, 50)
        ));

        List<Movement> found = movementRepository.findByBranchAndProduct(savedBranch, savedProduct);

        assertEquals(2, found.size());
        Movement movement = found.get(0);
        assertEquals(OriginType.INVOICE, movement.getOriginType());
        assertEquals(savedInvoice, movement.getInvoice());
        assertNull(movement.getRequest());
        assertEquals(savedBranch, movement.getBranch());
        assertNotNull(movement.getDateTime());
    }

    @Test
    void shouldReturnEmptyListWhenBranchAndProductHaveNoMovements() {
        List<Movement> found = movementRepository.findByBranchAndProduct(savedBranch, savedProduct);

        assertTrue(found.isEmpty());
    }
}
