package br.com.almoxarifado.exceptions;

public class BranchInvalidException extends RuntimeException {
    public BranchInvalidException(String message) {
        super(message);
    }
}
