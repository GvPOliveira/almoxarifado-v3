package br.com.almoxarifado.entities;

import jakarta.persistence.*;

@Entity
public class ProductRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "branch_product_id",nullable = false)
    private BranchProduct branchProduct;

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private Request request;

    @Column(nullable = false)
    private int requestedQuantity;

    @Column(nullable = false)
    private int attendedQuantity;

    public Request getRequest() {
        return request;
    }

    public ProductRequest(){}

    public ProductRequest(BranchProduct branchProduct, Request request, int requestedQuantity) {
        this.branchProduct = branchProduct;
        this.request = request;
        this.requestedQuantity = requestedQuantity;
    }

    public Long getId() {
        return id;
    }

    public BranchProduct getBranchProduct() {
        return branchProduct;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAttendedQuantity() {
        return attendedQuantity;
    }
}
