package br.com.almoxarifado.controllers;


import br.com.almoxarifado.dtos.product.ProductCreateDTO;
import br.com.almoxarifado.dtos.product.ProductResponseDTO;
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
    public ProductResponseDTO createProduct(@RequestBody @Valid ProductCreateDTO productCreateDTO) {
        return productService.createProduct(productCreateDTO.getCode(), productCreateDTO.getName());
    }

    @GetMapping("/{code}")
    public ProductResponseDTO getProductByCode(@PathVariable String code) {
        return productService.getProductByCode(code);
    }


    @GetMapping
    public List<ProductResponseDTO> getProducts() {
        return productService.getProducts();
    }


}
