package br.com.almoxarifado.exceptions.conflict;

public class ProductAlreadyExistsException extends ConflictException{

public ProductAlreadyExistsException(String mensagem){
    super(mensagem);
}

}

