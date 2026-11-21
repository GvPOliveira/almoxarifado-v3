package br.com.almoxarifado.exceptions.conflict;

public class InvoiceAlreadyExistsException extends ConflictException {
    public InvoiceAlreadyExistsException(String message) {
        super(message);
    }
}
