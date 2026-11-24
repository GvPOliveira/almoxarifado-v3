package br.com.almoxarifado.exceptions.notfound;

public class InvoiceNotFoundException extends NotFoundException{
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
