package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Branch;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BranchRepositoryTest {

    @Autowired
    private BranchRepository branchRepository;

    @Test
    void saveBranch() {
        Branch branch = new Branch("001", "Branch South");

        Branch savedBranch = branchRepository.save(branch);

        assertNotNull(savedBranch.getId());

        Optional<Branch> foundBranch =
                branchRepository.findByCode(savedBranch.getCode());

        assertTrue(foundBranch.isPresent());
        assertEquals("001", foundBranch.get().getCode());
        assertEquals("Branch South", foundBranch.get().getName());
    }
}
