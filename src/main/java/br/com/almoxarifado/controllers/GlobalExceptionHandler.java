package br.com.almoxarifado.controllers;

import br.com.almoxarifado.dtos.common.ErrorResponseDto;
import br.com.almoxarifado.dtos.common.ValidationErrorResponseDto;
import br.com.almoxarifado.exceptions.invalid.InvalidException;
import br.com.almoxarifado.exceptions.conflict.ConflictException;
import br.com.almoxarifado.exceptions.notfound.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String errorName = fieldError.getField();
            String errorReason = fieldError.getDefaultMessage();
            errors.put(errorName, errorReason);
        }
        ValidationErrorResponseDto validationErrorResponseDto = new ValidationErrorResponseDto(errors);
        ResponseEntity<Object> responseEntity =
                new ResponseEntity<>(validationErrorResponseDto, HttpStatus.BAD_REQUEST);
        return responseEntity;
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponseDto> handlerConflict(ConflictException
                                                                    conflictException) {
        ErrorResponseDto error = new ErrorResponseDto(conflictException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidException.class)
    public ResponseEntity<ErrorResponseDto> handlerInvalid(InvalidException
                                                                   invalidException) {
        ErrorResponseDto error = new ErrorResponseDto(invalidException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handlerNotFound(NotFoundException
                                                                    notFoundException) {
        ErrorResponseDto error = new ErrorResponseDto(notFoundException.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDto> handlerDataIntegrityViolation(DataIntegrityViolationException dataException) {
        log.warn("Violação de integridade de dados", dataException);
        ErrorResponseDto error = new ErrorResponseDto("Os dados enviados conflitam com informações já cadastradas.");
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handlerException(Exception exception) {
        log.error(exception.getMessage(), exception);
        ErrorResponseDto error = new ErrorResponseDto("Erro interno, tente novamente!");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
