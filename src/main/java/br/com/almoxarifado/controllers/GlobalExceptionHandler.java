package br.com.almoxarifado.controllers;

import br.com.almoxarifado.dtos.common.ErrorResponseDTO;
import br.com.almoxarifado.dtos.common.ValidationErrorResponseDTO;
import br.com.almoxarifado.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDTO> handlerValidation(MethodArgumentNotValidException
                                                                                methodArgumentNotValidException) {
        Map<String, String> errors = new HashMap<>();
        List<FieldError> fieldErrors = methodArgumentNotValidException.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String errorName = fieldError.getField();
            String errorReason = fieldError.getDefaultMessage();
            errors.put(errorName, errorReason);
        }
        ValidationErrorResponseDTO validationErrorResponseDTO = new ValidationErrorResponseDTO(errors);
        ResponseEntity<ValidationErrorResponseDTO> responseEntity =
                new ResponseEntity<>(validationErrorResponseDTO, HttpStatus.BAD_REQUEST);
        return responseEntity;
    }

    @ExceptionHandler(BranchAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handlerBranchAlreadyExists(BranchAlreadyExistsException
                                                                               branchAlreadyExistsException) {
        ErrorResponseDTO error = new ErrorResponseDTO(branchAlreadyExistsException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BranchInvalidException.class)
    public ResponseEntity<ErrorResponseDTO> handlerBranchInvalid(BranchInvalidException
                                                                         branchInvalidException) {
        ErrorResponseDTO error = new ErrorResponseDTO(branchInvalidException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BranchNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handlerBranchNotFound(BranchNotFoundException
                                                                          branchNotFoundException) {
        ErrorResponseDTO error = new ErrorResponseDTO(branchNotFoundException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }


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
