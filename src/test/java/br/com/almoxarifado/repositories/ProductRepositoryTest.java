package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;


@Test
    void saveProduct(){
    Product product = new Product("P001", "Teclado mecanico");
    Product savedProduct = productRepository.save(product);
    assertNotNull(savedProduct.getId());

    Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

    assertTrue(foundProduct.isPresent());
    assertEquals("P001", foundProduct.get().getCode());
    assertEquals("Teclado mecanico", foundProduct.get().getName());

}


}
