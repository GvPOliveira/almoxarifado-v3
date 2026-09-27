package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Branch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {
    Optional<Branch> findByCode(String code);
}
