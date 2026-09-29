package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.ProductInvoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductInvoiceRepository extends JpaRepository<ProductInvoice, Long> {
    List<ProductInvoice> findByInvoiceId(Long invoiceId);
}
