package br.com.almoxarifado.mappers;

import br.com.almoxarifado.dtos.branch.BranchResponseDto;
import br.com.almoxarifado.entities.Branch;
import org.springframework.stereotype.Component;

@Component
public class BranchMapper {
    public BranchResponseDto toDto(Branch branch) {
        return new BranchResponseDto(branch.getCode(), branch.getName());
    }
}
