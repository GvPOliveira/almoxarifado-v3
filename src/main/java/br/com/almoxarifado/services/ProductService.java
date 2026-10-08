package br.com.almoxarifado.services;


import br.com.almoxarifado.dtos.ProductResponseDTO;
import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.exceptions.ProductAlreadyExistsException;
import br.com.almoxarifado.exceptions.ProductNotFoundException;
import br.com.almoxarifado.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public ProductResponseDTO getProductByCode(String code) {
        Product product = productRepository.findByCode(code).orElseThrow(() -> new ProductNotFoundException("Esse código não existe."));
        return new ProductResponseDTO(product.getCode(), product.getName());
    }

    public ProductResponseDTO createProduct(String code, String name) {
        boolean existsCode = productRepository.existsByCode(code);
        if (existsCode) {
            throw new ProductAlreadyExistsException("Esse código já existe.");
        }
        Product product = new Product(code, name);
        Product savedProduct = productRepository.save(product);
        return new ProductResponseDTO(savedProduct.getCode(), savedProduct.getName());
    }

    public List<ProductResponseDTO> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(p -> new ProductResponseDTO(p.getCode(), p.getName()))
                .toList();
    }


}
