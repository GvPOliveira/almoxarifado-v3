package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.BranchProduct;
import br.com.almoxarifado.entities.Movement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovementRepository extends JpaRepository<Movement, UUID> {
            List<Movement> findByBranchProduct(BranchProduct branchProduct);
}
