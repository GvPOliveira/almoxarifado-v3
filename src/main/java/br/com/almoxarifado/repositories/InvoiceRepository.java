package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByNumberAndSupplierCnpj(String number, String supplierCnpj);
}
