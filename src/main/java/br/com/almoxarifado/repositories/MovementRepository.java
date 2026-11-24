package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.entities.Movement;
import br.com.almoxarifado.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovementRepository extends JpaRepository<Movement, UUID> {
    List<Movement> findByBranchAndProduct(Branch branch, Product product);
}
