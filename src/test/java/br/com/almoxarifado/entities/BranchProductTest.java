package br.com.almoxarifado.entities;

import br.com.almoxarifado.exceptions.invalid.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BranchProductTest {

    private BranchProduct branchProduct;

    @BeforeEach
    void setUp() {
        Product product = new Product("P001", "Parafuso");
        Branch branch = new Branch("01", "Branch South");
        branchProduct = new BranchProduct(branch, product, "P001.C023.A.D01");
    }

    @Test
    void shouldIncreaseQuantityWhenAmountIsPositive() {
        branchProduct.increaseQuantity(5);
        branchProduct.increaseQuantity(3);

        assertEquals(8, branchProduct.getQuantity());
    }

    @Test
    void shouldThrowInvalidQuantityWhenAmountIsZero() {
        assertThrows(InvalidQuantityException.class, () -> branchProduct.increaseQuantity(0));
        assertEquals(0, branchProduct.getQuantity());
    }

    @Test
    void shouldThrowInvalidQuantityWhenAmountIsNegative() {
        assertThrows(InvalidQuantityException.class, () -> branchProduct.increaseQuantity(-1));
        assertEquals(0, branchProduct.getQuantity());
    }
}
