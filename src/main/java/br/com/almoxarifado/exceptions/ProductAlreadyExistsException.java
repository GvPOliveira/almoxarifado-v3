package br.com.almoxarifado.exceptions;

public class ProductAlreadyExistsException extends RuntimeException{

public ProductAlreadyExistsException(String mensagem){
    super(mensagem);
}

}

