package br.com.almoxarifado.entities;

import jakarta.persistence.*;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"number", "supplier"}
        )
)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String number;
    @Column(nullable = false)
    private String supplier;
    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;
    private boolean processed;
    private boolean reversed;

    public Invoice() {
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public String getSupplier() {
        return supplier;
    }

    public Branch getBranch() {
        return branch;
    }

    public boolean isProcessed() {
        return processed;
    }

    public boolean isReversed() {
        return reversed;
    }
}
