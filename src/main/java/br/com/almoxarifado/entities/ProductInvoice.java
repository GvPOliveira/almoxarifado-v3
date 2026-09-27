package br.com.almoxarifado.entities;

import br.com.almoxarifado.enums.Destination;
import jakarta.persistence.*;

@Entity
public class ProductInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "invoice_id",nullable = false)
    private Invoice invoice;
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @Column(nullable = false)
    private int quantity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Destination destination;

    public ProductInvoice(){}

    public Long getId() {
        return id;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public Destination getDestination() {
        return destination;
    }
}
