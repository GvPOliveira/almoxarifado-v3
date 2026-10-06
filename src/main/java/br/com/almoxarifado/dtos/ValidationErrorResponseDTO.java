package br.com.almoxarifado.dtos;

import java.util.Map;

public class ValidationErrorResponseDTO{
    private Map<String, String> errors;

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public ValidationErrorResponseDTO(Map<String, String> errors){
        this.errors = errors;
    }

}
