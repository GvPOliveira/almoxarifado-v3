package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.BranchProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchProductRepository extends JpaRepository<BranchProduct, Long> {
    Optional<BranchProduct> findByBranchIdAndProductId(Long branchId, Long productId);

}
