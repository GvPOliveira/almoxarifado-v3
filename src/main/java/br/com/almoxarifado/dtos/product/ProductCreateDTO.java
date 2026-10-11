package br.com.almoxarifado.dtos.product;

import jakarta.validation.constraints.NotBlank;

public class ProductCreateDTO {

    @NotBlank
    private String code;
    @NotBlank
    private String name;


    public ProductCreateDTO(){}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
