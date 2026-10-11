package br.com.almoxarifado.services;

import br.com.almoxarifado.dtos.branch.BranchResponseDTO;
import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.exceptions.BranchAlreadyExistsException;
import br.com.almoxarifado.exceptions.BranchInvalidException;
import br.com.almoxarifado.exceptions.BranchNotFoundException;
import br.com.almoxarifado.repositories.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }


    public BranchResponseDTO createBranch(String code, String name) {
        if (code == null) {
            throw new BranchInvalidException("O código digitado está inválido.");
        }
        if (name == null) {
            throw new BranchInvalidException("O nome digitado está inválido.");
        }
        code = code.trim();
        name = name.trim();

        if(code.isBlank()){
            throw new BranchInvalidException("O código está vazio.");
        }
        if(name.isBlank()){
            throw new BranchInvalidException("O nome está vazio.");
        }

        boolean existsBranch = branchRepository.existsByCodeIgnoreCase(code);
        if (existsBranch) {
            throw new BranchAlreadyExistsException("Essa filial com esse código já existe.");
        }
        boolean existsBranchName = branchRepository.existsByNameIgnoreCase(name);
        if (existsBranchName) {
            throw new BranchAlreadyExistsException("Essa filial já existe com esse nome.");
        }
        Branch savedBranch = branchRepository.save(new Branch(code, name));
        return new BranchResponseDTO(savedBranch.getCode(), savedBranch.getName());
    }

    public BranchResponseDTO findByCode(String code) {
        Branch branch = branchRepository.findByCodeIgnoreCase(code).orElseThrow(() -> new
                BranchNotFoundException("Filial não encontrada."));
        return new BranchResponseDTO(branch.getCode(), branch.getName());
    }

    public List<BranchResponseDTO> findAll() {
        return branchRepository.findAll().stream()
                .map(branch -> new BranchResponseDTO(branch.getCode(), branch.getName()))
                .toList();
    }


}
