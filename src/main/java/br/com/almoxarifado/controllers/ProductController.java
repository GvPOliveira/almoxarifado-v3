package br.com.almoxarifado.controllers;


import br.com.almoxarifado.dtos.product.ProductCreateDto;
import br.com.almoxarifado.dtos.product.ProductResponseDto;
import br.com.almoxarifado.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponseDto createProduct(@RequestBody @Valid ProductCreateDto productCreateDto) {
        return productService.createProduct(productCreateDto.code(), productCreateDto.name());
    }

    @GetMapping("/{code}")
    public ProductResponseDto getProductByCode(@PathVariable String code) {
        return productService.getProductByCode(code);
    }


    @GetMapping
    public List<ProductResponseDto> getProducts() {
        return productService.getProducts();
    }


}
