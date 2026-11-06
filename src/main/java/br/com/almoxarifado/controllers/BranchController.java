package br.com.almoxarifado.controllers;

import br.com.almoxarifado.dtos.branch.BranchCreateDto;
import br.com.almoxarifado.dtos.branch.BranchResponseDto;
import br.com.almoxarifado.services.BranchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BranchResponseDto saveBranch(@RequestBody @Valid BranchCreateDto branchCreateDto) {
        return branchService.createBranch(branchCreateDto.code(), branchCreateDto.name());
    }

    @GetMapping("/{code}")
    public BranchResponseDto findBranchByCode(@PathVariable String code) {
        return branchService.findBranchByCode(code);
    }

    @GetMapping
    public List<BranchResponseDto> findAll() {
        return branchService.findAll();
    }


}
