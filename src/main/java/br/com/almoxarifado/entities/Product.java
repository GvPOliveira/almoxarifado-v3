package br.com.almoxarifado.entities;

import jakarta.persistence.*;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private String name;

    public Product(){}

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
