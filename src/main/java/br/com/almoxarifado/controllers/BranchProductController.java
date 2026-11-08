package br.com.almoxarifado.controllers;

import br.com.almoxarifado.dtos.branchproduct.BranchProductCreateDto;
import br.com.almoxarifado.dtos.branchproduct.BranchProductResponseDto;
import br.com.almoxarifado.services.BranchProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/branches/{branchCode}/products")
public class BranchProductController {

    private final BranchProductService branchProductService;

    public BranchProductController(BranchProductService branchProductService) {
        this.branchProductService = branchProductService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BranchProductResponseDto saveBranchProduct(@RequestBody @Valid BranchProductCreateDto branchProductCreateDto, @PathVariable String branchCode) {
        return branchProductService.createBranchProduct(branchCode, branchProductCreateDto.productCode(), branchProductCreateDto.location());
    }
}
