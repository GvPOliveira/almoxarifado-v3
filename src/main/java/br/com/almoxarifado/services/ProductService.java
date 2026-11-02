package br.com.almoxarifado.services;


import br.com.almoxarifado.dtos.product.ProductResponseDTO;
import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.exceptions.ProductAlreadyExistsException;
import br.com.almoxarifado.exceptions.ProductNotFoundException;
import br.com.almoxarifado.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public ProductResponseDTO getProductByCode(String code) {
        String normalizedCode = normalizeCode(code);
        Product product = productRepository.findByCode(normalizedCode).orElseThrow(() -> new ProductNotFoundException("Esse código não existe."));
        return new ProductResponseDTO(product.getCode(), product.getName());
    }

    public ProductResponseDTO createProduct(String code, String name) {
        String normalizedCode = normalizeCode(code);
        String normalizedName = normalizeName(name);
        boolean existsCode = productRepository.existsByCode(normalizedCode);
        if (existsCode) {
            throw new ProductAlreadyExistsException("Esse código já existe.");
        }
        Product product = new Product(normalizedCode, normalizedName);
        Product savedProduct = productRepository.save(product);
        return new ProductResponseDTO(savedProduct.getCode(), savedProduct.getName());
    }

    public List<ProductResponseDTO> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(p -> new ProductResponseDTO(p.getCode(), p.getName()))
                .toList();
    }


    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
    private String normalizeName(String name){
        return name.trim();
    }

}
