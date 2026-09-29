package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.BranchProduct;
import br.com.almoxarifado.entities.ProductRequest;
import br.com.almoxarifado.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRequestRepository extends JpaRepository<ProductRequest, Long> {
    Optional<ProductRequest> findByBranchProductAndRequest(BranchProduct branchProduct, Request request);
}
