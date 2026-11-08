package br.com.almoxarifado.exceptions.conflict;

public class BranchProductAlreadyExistsException extends ConflictException {
    public BranchProductAlreadyExistsException(String message) {
        super(message);
    }
}
