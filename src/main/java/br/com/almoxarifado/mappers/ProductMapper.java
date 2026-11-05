package br.com.almoxarifado.mappers;

import br.com.almoxarifado.dtos.product.ProductResponseDto;
import br.com.almoxarifado.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(product.getCode(), product.getName());
    }
}
