package br.com.almoxarifado.repositories;


import br.com.almoxarifado.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class ProductRequestRepositoryTest {


    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private BranchProductRepository branchProductRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private ProductRequestRepository productRequestRepository;


    @Test
    void findByBranchProductAndRequest() {
        Product product = new Product("P020", "Telemoto");
        Product savedProduct = productRepository.save(product);
        Branch branch = new Branch("001", "Branch South");
        Branch savedBranch = branchRepository.save(branch);
        BranchProduct branchProduct = new BranchProduct(savedBranch, savedProduct, 300, "P-030");
        BranchProduct savedBranchProduct = branchProductRepository.save(branchProduct);
        Request request = new Request("Request", savedBranch);
        Request savedRequest = requestRepository.save(request);


        ProductRequest productRequest = new ProductRequest(savedBranchProduct, savedRequest, 200);
        productRequestRepository.save(productRequest);

        Optional<ProductRequest> foundProductRequest = productRequestRepository.findByBranchProductAndRequest(savedBranchProduct,
                savedRequest);

        assertTrue(foundProductRequest.isPresent());
        assertEquals("P020", foundProductRequest.get().getBranchProduct().getProduct().getCode());
        assertEquals("001", foundProductRequest.get().getBranchProduct().getBranch().getCode());
        assertEquals("Request", foundProductRequest.get().getRequest().getNumber());
        assertEquals(300, foundProductRequest.get().getBranchProduct().getQuantity());


    }
    @Test
    void findByBranchProductAndRequestNotFound() {
        Product product = new Product("P020", "Telemoto");
        Product savedProduct = productRepository.save(product);
        Branch branch = new Branch("001", "Branch South");
        Branch savedBranch = branchRepository.save(branch);
        BranchProduct branchProduct = new BranchProduct(savedBranch, savedProduct, 300, "P-030");
        BranchProduct savedBranchProduct = branchProductRepository.save(branchProduct);
        Request request = new Request("Request", savedBranch);
        Request savedRequest = requestRepository.save(request);
        Optional<ProductRequest> foundProductRequest = productRequestRepository.findByBranchProductAndRequest(savedBranchProduct,
                savedRequest);
        assertTrue(foundProductRequest.isEmpty());
    }


}
