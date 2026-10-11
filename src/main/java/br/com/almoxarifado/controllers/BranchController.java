package br.com.almoxarifado.controllers;

import br.com.almoxarifado.dtos.branch.BranchCreateDTO;
import br.com.almoxarifado.dtos.branch.BranchResponseDTO;
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
    public BranchResponseDTO saveBranch(@RequestBody @Valid BranchCreateDTO branchCreateDTO) {
        return branchService.createBranch(branchCreateDTO.getCode(), branchCreateDTO.getName());
    }

    @GetMapping("/{code}")
    public BranchResponseDTO findByCode(@PathVariable String code) {
        return branchService.findByCode(code);
    }

    @GetMapping
    public List<BranchResponseDTO> findAll() {
        return branchService.findAll();
    }


}
