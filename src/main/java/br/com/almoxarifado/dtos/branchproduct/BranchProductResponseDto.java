package br.com.almoxarifado.dtos.branchproduct;

public record BranchProductResponseDto(String productCode,
                                        String branchCode, String location, int quantity) {
}
