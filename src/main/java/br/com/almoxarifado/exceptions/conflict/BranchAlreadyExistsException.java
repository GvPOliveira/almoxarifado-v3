package br.com.almoxarifado.exceptions.conflict;

public class BranchAlreadyExistsException extends ConflictException {
    public BranchAlreadyExistsException(String message) {
        super(message);
    }
}
