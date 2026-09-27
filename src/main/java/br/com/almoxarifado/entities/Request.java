package br.com.almoxarifado.entities;

import jakarta.persistence.*;

@Entity
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String number;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    private boolean processed;
    private boolean reverted;

    public Request(){}

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Branch getBranch() {
        return branch;
    }

    public boolean isProcessed() {
        return processed;
    }

    public boolean isReverted() {
        return reverted;
    }
}

