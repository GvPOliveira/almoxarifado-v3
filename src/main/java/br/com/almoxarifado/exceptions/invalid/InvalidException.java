package br.com.almoxarifado.exceptions.invalid;

public abstract class InvalidException extends RuntimeException {
    public InvalidException(String message) {
        super(message);
    }
}
