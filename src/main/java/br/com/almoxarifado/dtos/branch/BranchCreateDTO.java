package br.com.almoxarifado.dtos.branch;

import jakarta.validation.constraints.NotBlank;

public class BranchCreateDTO {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    public BranchCreateDTO() {
    }

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
