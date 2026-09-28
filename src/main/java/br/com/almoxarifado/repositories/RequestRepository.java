package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Optional<Request> findByNumber(String number);
}
