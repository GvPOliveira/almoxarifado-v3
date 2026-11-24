package br.com.almoxarifado.entities;

import br.com.almoxarifado.enums.MovementType;
import br.com.almoxarifado.enums.OriginType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Movement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MovementType movementType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OriginType originType;

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private Request request;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    public Movement() {
    }

    private Movement(MovementType movementType, OriginType originType, Invoice invoice, Request request, Branch branch, Product product, int quantity) {
        this.movementType = movementType;
        this.originType = originType;
        this.invoice = invoice;
        this.request = request;
        this.branch = branch;
        this.product = product;
        this.quantity = quantity;
        this.dateTime = LocalDateTime.now();
    }

    public static Movement fromInvoice(MovementType movementType, Invoice invoice,
                                       Product product, int quantity) {
        return new Movement(movementType, OriginType.INVOICE, invoice, null, invoice.getBranch(), product, quantity);
    }

    public static Movement fromRequest(MovementType movementType, Request request,
                                       Product product, int quantity) {
        return new Movement(movementType, OriginType.REQUEST, null, request, request.getBranch(), product, quantity);
    }

    public UUID getUuid() {
        return uuid;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public int getQuantity() {
        return quantity;
    }

    public OriginType getOriginType() {
        return originType;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public Request getRequest() {
        return request;
    }

    public Branch getBranch() {
        return branch;
    }

    public Product getProduct() {
        return product;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

}
