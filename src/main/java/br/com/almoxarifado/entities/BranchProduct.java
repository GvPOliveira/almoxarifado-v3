package br.com.almoxarifado.entities;

import jakarta.persistence.*;

@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"branch_id", "product_id"}
        )
)
public class BranchProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    private String location;

    public BranchProduct() {
    }

    public BranchProduct(Branch branch, Product product, int quantity, String location) {
        this.branch = branch;
        this.product = product;
        this.quantity = quantity;
        this.location = location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public Branch getBranch() {
        return branch;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getLocation() {
        return location;
    }
}
