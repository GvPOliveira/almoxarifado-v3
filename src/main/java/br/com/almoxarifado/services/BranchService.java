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
        if (code == null || code.isBlank()) {
            throw new BranchInvalidException("O código da filial é obrigatório.");
        }
        if (name == null || name.isBlank()) {
            throw new BranchInvalidException("O nome da filial é obrigatório.");
        }
        String normalizedCode = normalizeCode(code);
        String normalizedName = normalizeName(name);

        boolean existsBranch = branchRepository.existsByCode(normalizedCode);
        if (existsBranch) {
            throw new BranchAlreadyExistsException(
                    "Já existe uma filial com o código '%s'.".formatted(normalizedCode));
        }
        boolean existsBranchName = branchRepository.existsByNameIgnoreCase(normalizedName);
        if (existsBranchName) {
            throw new BranchAlreadyExistsException(
                    "Já existe uma filial com o nome '%s'.".formatted(normalizedName));
        }
        Branch savedBranch = branchRepository.save(new Branch(normalizedCode, normalizedName));
        return new BranchResponseDTO(savedBranch.getCode(), savedBranch.getName());
    }

    public BranchResponseDTO findByCode(String code) {
        String normalizedCode = normalizeCode(code);
        Branch branch = branchRepository.findByCode(normalizedCode).orElseThrow(() -> new
                BranchNotFoundException("Nenhuma filial encontrada com o código '%s'.".formatted(normalizedCode)));
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
