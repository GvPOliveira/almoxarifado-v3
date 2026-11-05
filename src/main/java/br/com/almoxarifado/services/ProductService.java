package br.com.almoxarifado.services;


import br.com.almoxarifado.dtos.product.ProductResponseDto;
import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.exceptions.conflict.ProductAlreadyExistsException;
import br.com.almoxarifado.exceptions.invalid.ProductInvalidException;
import br.com.almoxarifado.exceptions.notfound.ProductNotFoundException;
import br.com.almoxarifado.mappers.ProductMapper;
import br.com.almoxarifado.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ProductService {


    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }


    public ProductResponseDto getProductByCode(String code) {
        String normalizedCode = normalizeCode(code);
        Product product = productRepository.findByCode(normalizedCode).orElseThrow(() -> new ProductNotFoundException(
                "Nenhum produto encontrado com o código '%s'.".formatted(normalizedCode)));
        return productMapper.toDto(product);
    }

    public ProductResponseDto createProduct(String code, String name) {
        if (code == null || code.isBlank()) {
            throw new ProductInvalidException("O código do produto é obrigatório.");
        }
        if (name == null || name.isBlank()) {
            throw new ProductInvalidException("O nome do produto é obrigatório.");
        }

        String normalizedCode = normalizeCode(code);
        String normalizedName = normalizeName(name);
        boolean existsCode = productRepository.existsByCode(normalizedCode);
        if (existsCode) {
            throw new ProductAlreadyExistsException(
                    "Já existe um produto com o código '%s'.".formatted(normalizedCode));
        }
        Product product = new Product(normalizedCode, normalizedName);
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public List<ProductResponseDto> getProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }


    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeName(String name) {
        return name.trim();
    }

}
