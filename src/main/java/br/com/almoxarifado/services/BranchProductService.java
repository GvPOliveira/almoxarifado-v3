package br.com.almoxarifado.services;

import br.com.almoxarifado.dtos.branchproduct.BranchProductResponseDto;
import br.com.almoxarifado.entities.Branch;
import br.com.almoxarifado.entities.BranchProduct;
import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.exceptions.conflict.BranchProductAlreadyExistsException;
import br.com.almoxarifado.mappers.BranchProductMapper;
import br.com.almoxarifado.repositories.BranchProductRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class BranchProductService {


    private final BranchProductRepository branchProductRepository;

    private final ProductService productService;

    private final BranchService branchService;

    private final BranchProductMapper branchProductMapper;

    public BranchProductService(BranchProductRepository branchProductRepository, ProductService productService,
                                BranchService branchService, BranchProductMapper branchProductMapper) {
        this.branchProductRepository = branchProductRepository;
        this.productService = productService;
        this.branchService = branchService;
        this.branchProductMapper = branchProductMapper;
    }


    public BranchProductResponseDto createBranchProduct(String branchCode, String productCode, String location) {
        Product product = productService.findProductEntityByCode(productCode);
        Branch branch = branchService.findBranchEntityByCode(branchCode);
        boolean exists = branchProductRepository.existsByBranchIdAndProductId(branch.getId(),
                product.getId());
        if (exists) {
            throw new BranchProductAlreadyExistsException("Já existe um produto com esse código '%s' nessa filial '%s'.".formatted(product.getCode(), branch.getCode()));
        }
        String normalizedLocation;
        if (location == null || location.isBlank()) {
            normalizedLocation = null;
        } else {
            normalizedLocation = location.trim().toUpperCase(Locale.ROOT);
        }
        BranchProduct branchProduct = new BranchProduct(branch, product, normalizedLocation);
        BranchProduct savedBranchProduct = branchProductRepository.save(branchProduct);
        return branchProductMapper.toDto(savedBranchProduct);
    }
}
