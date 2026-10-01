package br.com.almoxarifado.services;


import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.exceptions.ProductAlreadyExistsException;
import br.com.almoxarifado.repositories.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Product createProduct(String code, String name) {
        boolean existsCode = productRepository.existsByCode(code);
        if (existsCode) {
            throw new ProductAlreadyExistsException("Esse código já existe.");
        }
        Product product = new Product(code, name);
        return productRepository.save(product);
    }


}
