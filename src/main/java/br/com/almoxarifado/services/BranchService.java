package br.com.almoxarifado.services;

import br.com.almoxarifado.dtos.branch.BranchResponseDto;
import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.exceptions.conflict.BranchAlreadyExistsException;
import br.com.almoxarifado.exceptions.invalid.BranchInvalidException;
import br.com.almoxarifado.exceptions.notfound.BranchNotFoundException;
import br.com.almoxarifado.mappers.BranchMapper;
import br.com.almoxarifado.repositories.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class BranchService {

    private final BranchRepository branchRepository;
    private final BranchMapper branchMapper;

    public BranchService(BranchRepository branchRepository, BranchMapper branchMapper) {
        this.branchRepository = branchRepository;
        this.branchMapper = branchMapper;
    }


    public BranchResponseDto createBranch(String code, String name) {
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
        return branchMapper.toDto(savedBranch);
    }

    public BranchResponseDto findBranchByCode(String code) {
        Branch branch = findBranchEntityByCode(code);
        return branchMapper.toDto(branch);
    }

    public Branch findBranchEntityByCode(String code) {
        String normalizedCode = normalizeCode(code);
        return branchRepository.findByCode(normalizedCode).orElseThrow(() -> new
                BranchNotFoundException("Nenhuma filial encontrada com o código '%s'.".formatted(normalizedCode)));
    }


    public List<BranchResponseDto> findAll() {
        return branchRepository.findAll()
                .stream()
                .map(branchMapper::toDto)
                .toList();
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeName(String name) {
        return name.trim();
    }


}
