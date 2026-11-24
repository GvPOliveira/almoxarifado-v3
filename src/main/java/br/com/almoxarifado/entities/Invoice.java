package br.com.almoxarifado.entities;

import br.com.almoxarifado.enums.Destination;
import br.com.almoxarifado.exceptions.conflict.InvoiceAlreadyProcessedException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
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
    private List<ProductInvoice> productInvoices = new ArrayList<>();
    private BigDecimal totalValue = BigDecimal.ZERO;
    private boolean processed;
    private boolean reversed;

    public Invoice() {
    }

    public Invoice(String number, Supplier supplier, Branch branch) {
        this.number = number;
        this.supplier = supplier;
        this.branch = branch;
    }

    public void addItem(Product product, int quantity, Destination destination, BigDecimal unitPrice) {
        ProductInvoice productInvoice = new ProductInvoice(this, product, quantity, destination, unitPrice);
        productInvoices.add(productInvoice);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.totalValue = this.totalValue.add(totalPrice);

    }

    public void markAsProcessed() {
        if (this.processed) {
            throw new InvoiceAlreadyProcessedException("A nota fiscal de número '%s' já foi processada.".formatted(this.number));
        }
        this.processed = true;
    }

    public List<ProductInvoice> getProductInvoices() {
        return List.copyOf(productInvoices);
    }

    public BigDecimal getTotalValue() {
        return totalValue;
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
