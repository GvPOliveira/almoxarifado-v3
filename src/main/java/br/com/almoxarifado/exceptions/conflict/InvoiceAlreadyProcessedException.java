package br.com.almoxarifado.exceptions.conflict;

public class InvoiceAlreadyProcessedException extends ConflictException{
    public InvoiceAlreadyProcessedException(String message) {
        super(message);
    }
}
