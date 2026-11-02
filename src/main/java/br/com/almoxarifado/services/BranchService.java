package br.com.almoxarifado.services;

import br.com.almoxarifado.dtos.branch.BranchResponseDTO;
import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.exceptions.conflict.BranchAlreadyExistsException;
import br.com.almoxarifado.exceptions.invalid.BranchInvalidException;
import br.com.almoxarifado.exceptions.notfound.BranchNotFoundException;
import br.com.almoxarifado.repositories.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

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
        String normalizedCode = normalizeCode(code);
        String normalizedName = normalizeName(name);
        if (normalizedCode.isBlank()) {
            throw new BranchInvalidException("O código está vazio.");
        }
        if (normalizedName.isBlank()) {
            throw new BranchInvalidException("O nome está vazio.");
        }

        boolean existsBranch = branchRepository.existsByCode(normalizedCode);
        if (existsBranch) {
            throw new BranchAlreadyExistsException("Essa filial com esse código já existe.");
        }
        boolean existsBranchName = branchRepository.existsByNameIgnoreCase(normalizedName);
        if (existsBranchName) {
            throw new BranchAlreadyExistsException("Essa filial já existe com esse nome.");
        }
        Branch savedBranch = branchRepository.save(new Branch(code, name));
        return new BranchResponseDTO(savedBranch.getCode(), savedBranch.getName());
    }

    public BranchResponseDTO findByCode(String code) {
        String normalizedCode = normalizeCode(code);
        Branch branch = branchRepository.findByCode(normalizedCode).orElseThrow(() -> new
                BranchNotFoundException("Filial não encontrada."));
        return new BranchResponseDTO(branch.getCode(), branch.getName());
    }

    public List<BranchResponseDTO> findAll() {
        return branchRepository.findAll().stream()
                .map(branch -> new BranchResponseDTO(branch.getCode(), branch.getName()))
                .toList();
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeName(String name) {
        return name.trim();
    }


}
