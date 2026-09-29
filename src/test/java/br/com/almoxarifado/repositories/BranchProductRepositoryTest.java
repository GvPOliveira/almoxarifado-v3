package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.entities.BranchProduct;
import br.com.almoxarifado.entities.Product;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BranchProductRepositoryTest {

    @Autowired
    private BranchProductRepository branchProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BranchRepository branchRepository;

    @Test
    void createBranchProduct() {
        Product product = new Product("P003", "Teclado");
        Product savedProduct = productRepository.save(product);
        Branch branch = new Branch("001", "Branch South");
        Branch savedBranch = branchRepository.save(branch);
        BranchProduct branchProduct = new BranchProduct(savedBranch, savedProduct,
                200, "CORREDOR 2");
        BranchProduct savedBranchProduct = branchProductRepository.save(branchProduct);
        assertNotNull(savedBranchProduct);
        assertEquals("P003", savedBranchProduct.getProduct().getCode());
        assertEquals("001", savedBranchProduct.getBranch().getCode());
        assertEquals(200, savedBranchProduct.getQuantity());
    }

    @Test
    void findByBranchProductWithBranchIdAndProductId() {
        Product product = new Product("P002", "Teclado Mecatronico");
        Product savedProduct = productRepository.save(product);
        Branch branch = new Branch("001", "Branch South");
        Branch savedBranch = branchRepository.save(branch);
        BranchProduct branchProduct = new BranchProduct(savedBranch, savedProduct, 200, "CORREDOR 2");
        branchProductRepository.save(branchProduct);
        Optional<BranchProduct> foundBranchProduct = branchProductRepository.findByBranchIdAndProductId(
                savedBranch.getId(), savedProduct.getId()
        );

        assertTrue(foundBranchProduct.isPresent());
        assertEquals("P002", foundBranchProduct.get().getProduct().getCode());
        assertEquals("001", foundBranchProduct.get().getBranch().getCode());
        assertEquals(200, foundBranchProduct.get().getQuantity());

    }


}
