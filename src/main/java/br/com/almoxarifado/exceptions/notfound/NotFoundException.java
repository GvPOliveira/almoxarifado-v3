package br.com.almoxarifado.exceptions.notfound;

public abstract class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
