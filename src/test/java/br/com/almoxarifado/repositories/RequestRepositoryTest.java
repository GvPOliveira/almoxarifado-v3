package br.com.almoxarifado.repositories;

import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.entities.Request;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RequestRepositoryTest {


    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private BranchRepository branchRepository;


    @Test
    void foundByCodeRequest() {
        Branch branch = new Branch("001", "Branch South");
        Branch savedBranch = branchRepository.save(branch);
        Request request = new Request("555", savedBranch);
        requestRepository.save(request);
        Optional<Request> foundRequest = requestRepository.findByNumber("555");

        assertTrue(foundRequest.isPresent());
        assertEquals("001", foundRequest.get().getBranch().getCode());
        assertEquals("555", foundRequest.get().getNumber());
        assertFalse(foundRequest.get().isProcessed());
        assertFalse(foundRequest.get().isReverted());
    }

    @Test
    void foundRequestByCodeNotFound() {
        Optional<Request> request = requestRepository.findByNumber("6666");
        assertTrue(request.isEmpty());
    }


}
