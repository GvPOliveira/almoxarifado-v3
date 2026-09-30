package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.entities.BranchProduct;
import br.com.almoxarifado.entities.Movement;
import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.enums.MovementType;
import br.com.almoxarifado.enums.OriginType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class MovementRepositoryTest {


    @Autowired
    private BranchProductRepository branchProductRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private MovementRepository movementRepository;


    @Test
    void findByBranchProduct() {
        Product product = new Product("P030", "Teclado Mecanico");
        Product savedProduct = productRepository.save(product);
        Branch branch = new Branch("002", "Branch West");
        Branch savedBranch = branchRepository.save(branch);

        BranchProduct branchProduct = new BranchProduct(savedBranch, savedProduct, 200, "Prateleiro 30");
        BranchProduct savedBranchProduct = branchProductRepository.save(branchProduct);

        Movement movement1 = new Movement(MovementType.ENTRY, OriginType.INVOICE, "100", branchProduct, 200);
        Movement movement2 = new Movement(MovementType.ENTRY, OriginType.INVOICE, "200", branchProduct, 200);
        Movement movement3 = new Movement(MovementType.ENTRY, OriginType.INVOICE, "300", branchProduct, 200);
        Movement movement4 = new Movement(MovementType.ENTRY, OriginType.INVOICE, "400", branchProduct, 200);
        Movement movement5 = new Movement(MovementType.ENTRY, OriginType.INVOICE, "500", branchProduct, 200);
        List<Movement> movements = new ArrayList<>();
        movements.add(movement1);
        movements.add(movement2);
        movements.add(movement3);
        movements.add(movement4);
        movements.add(movement5);
        movementRepository.saveAll(movements);

        List<Movement> foundListMovementsByBranchProducts = movementRepository.findByBranchProduct(savedBranchProduct);

        assertFalse(foundListMovementsByBranchProducts.isEmpty());
        assertEquals(5, foundListMovementsByBranchProducts.size());
        assertEquals(MovementType.ENTRY, foundListMovementsByBranchProducts.get(0).getMovementType());
        assertEquals(branchProduct,foundListMovementsByBranchProducts.get(0).getBranchProduct());
    }

    @Test
    void findBranchProductNotMovements(){
        Product product = new Product("P030", "Teclado Mecanico");
        Product savedProduct = productRepository.save(product);
        Branch branch = new Branch("002", "Branch West");
        Branch savedBranch = branchRepository.save(branch);

        BranchProduct branchProduct = new BranchProduct(savedBranch, savedProduct, 200, "Prateleiro 30");
        BranchProduct savedBranchProduct = branchProductRepository.save(branchProduct);

        List<Movement> foundListMovementsByBranchProducts = movementRepository.findByBranchProduct(savedBranchProduct);

        assertTrue(foundListMovementsByBranchProducts.isEmpty());

    }


}
