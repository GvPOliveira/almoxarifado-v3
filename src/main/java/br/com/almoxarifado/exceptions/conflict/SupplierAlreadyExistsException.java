package br.com.almoxarifado.exceptions.conflict;

public class SupplierAlreadyExistsException extends ConflictException {
    public SupplierAlreadyExistsException(String message) {
        super(message);
    }
}
