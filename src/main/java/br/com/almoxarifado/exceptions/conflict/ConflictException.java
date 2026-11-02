package br.com.almoxarifado.exceptions.conflict;

public abstract class ConflictException extends RuntimeException{
    public ConflictException(String message){
        super(message);
    }
}
