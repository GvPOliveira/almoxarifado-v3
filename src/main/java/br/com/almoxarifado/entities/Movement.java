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

    @Column(nullable = false)
    private String originNumber;

    @ManyToOne
    @JoinColumn(name = "branch_product_id", nullable = false)
    private BranchProduct branchProduct;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    public Movement(){}

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

    public String getOriginNumber() {
        return originNumber;
    }

    public BranchProduct getBranchProduct() {
        return branchProduct;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDateTime getLocalDateTime() {
        return dateTime;
    }
}
