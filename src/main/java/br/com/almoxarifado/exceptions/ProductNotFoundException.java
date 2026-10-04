package br.com.almoxarifado.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String mensagem) {
        super(mensagem);
    }

}
