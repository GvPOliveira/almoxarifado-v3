package br.com.almoxarifado.mappers;

import br.com.almoxarifado.dtos.branchproduct.BranchProductResponseDto;
import br.com.almoxarifado.entities.BranchProduct;
import org.springframework.stereotype.Component;

@Component
public class BranchProductMapper {

    public BranchProductResponseDto toDto(BranchProduct branchProduct) {
        return new BranchProductResponseDto(branchProduct.getProduct().getCode(),
                branchProduct.getBranch().getCode(),
                branchProduct.getLocation(), branchProduct.getQuantity());
    }
}
