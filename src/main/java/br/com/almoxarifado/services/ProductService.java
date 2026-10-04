package br.com.almoxarifado.services;


import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.exceptions.ProductAlreadyExistsException;
import br.com.almoxarifado.exceptions.ProductNotFoundException;
import br.com.almoxarifado.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Product getProductByCode(String code) {
      return productRepository.findByCode(code).orElseThrow(() ->new ProductNotFoundException("Esse código não existe."));
    }

    public Product createProduct(String code, String name) {
        boolean existsCode = productRepository.existsByCode(code);
        if (existsCode) {
            throw new ProductAlreadyExistsException("Esse código já existe.");
        }
        Product product = new Product(code, name);
        return productRepository.save(product);
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }


}
