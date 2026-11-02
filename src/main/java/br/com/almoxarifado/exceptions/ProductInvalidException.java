package br.com.almoxarifado.exceptions;

public class ProductInvalidException extends RuntimeException {
    public ProductInvalidException(String message) {
        super(message);
    }
}
