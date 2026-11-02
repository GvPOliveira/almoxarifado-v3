package br.com.almoxarifado.exceptions.notfound;

public class ProductNotFoundException extends NotFoundException {
    public ProductNotFoundException(String mensagem) {
        super(mensagem);
    }

}
