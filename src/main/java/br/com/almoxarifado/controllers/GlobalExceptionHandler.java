package br.com.almoxarifado.controllers;

import br.com.almoxarifado.dtos.ErrorResponseDTO;
import br.com.almoxarifado.exceptions.ProductAlreadyExistsException;
import br.com.almoxarifado.exceptions.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductAlreadyExists(
            ProductAlreadyExistsException productAlreadyExistsException) {
        ErrorResponseDTO error = new ErrorResponseDTO(productAlreadyExistsException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleProductNotFound
            (ProductNotFoundException productNotFoundException) {
        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(productNotFoundException.getMessage());
        return new ResponseEntity<>(errorResponseDTO, HttpStatus.NOT_FOUND);
    }
}
