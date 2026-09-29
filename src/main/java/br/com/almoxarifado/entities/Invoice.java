package br.com.almoxarifado.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"number", "supplier_id"}
        )
)
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String number;
    @ManyToOne()
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.PERSIST)
    private List<ProductInvoice> productInvoices;
    private BigDecimal totalValue;
    private boolean processed;
    private boolean reversed;

    public Invoice() {
    }

    public Invoice(String number, Supplier supplier, Branch branch, BigDecimal totalValue) {
        this.number = number;
        this.supplier = supplier;
        this.branch = branch;
        this.totalValue = totalValue;
    }

    public Long getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public Supplier getSupplier() {
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
