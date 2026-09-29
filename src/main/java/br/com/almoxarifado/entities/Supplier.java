package br.com.almoxarifado.entities;


import jakarta.persistence.*;

@Entity
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String cnpj;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public Supplier() {
    }

    public Supplier(String name, String cnpj) {
        this.name = name;
        this.cnpj = cnpj;
    }
}
