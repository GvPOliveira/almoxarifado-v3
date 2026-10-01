package br.com.almoxarifado.controllers;


import br.com.almoxarifado.dtos.ErrorResponseDTO;
import br.com.almoxarifado.dtos.ProductCreateDTO;
import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.exceptions.ProductAlreadyExistsException;
import br.com.almoxarifado.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody ProductCreateDTO productCreateDTO) {
        return productService.createProduct(productCreateDTO.getCode(), productCreateDTO.getName());
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> errorResponse(
            ProductAlreadyExistsException productAlreadyExistsException) {
        ErrorResponseDTO error = new ErrorResponseDTO(productAlreadyExistsException.getMessage());
        ResponseEntity<ErrorResponseDTO> responseEntity = new ResponseEntity<>(error, HttpStatus.CONFLICT);
        return responseEntity;
    }
}
