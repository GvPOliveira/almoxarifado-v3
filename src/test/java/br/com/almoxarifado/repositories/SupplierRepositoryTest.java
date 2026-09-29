package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Supplier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
public class SupplierRepositoryTest {


    @Autowired
    private SupplierRepository supplierRepository;

    @Test
    void findByCnpj() {
        Supplier supplier = new Supplier("Mercado Livre", "08.028.003/0001-55");
        supplierRepository.save(supplier);
        Optional<Supplier> foundSupplier = supplierRepository.findByCnpj("08.028.003/0001-55");

        assertTrue(foundSupplier.isPresent());
        assertEquals("Mercado Livre", foundSupplier.get().getName());
    }

    @Test
    void findByCnpjNotFound() {
        Optional<Supplier> foundSupplier = supplierRepository.findByCnpj("08.028.003/0001-55");

        assertTrue(foundSupplier.isEmpty());
    }


}
