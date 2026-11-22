package br.com.almoxarifado.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void createSupplierAndBranchAndProducts() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": " Casas Bahia ",
                        "cnpj": "03.038.044/00ed-01"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/branches").content("""
                        {
                        "code": "001",
                        "name": "BranchSouth"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(
                        post("/products").content("""
                                {
                                    "code": "P001",
                                    "name": "BranchProduct"
                                }
                                """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/branches/{branchCode}/products", "001").content("""
                 {
                    "productCode": "P001",
                    "location": "C001.P023.C.D01"
                 }
                """).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());

        mockMvc.perform(
                        post("/products").content("""
                                {
                                    "code": "P002",
                                    "name": "Product"
                                }
                                """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldCreateInvoiceWhenDataIsValid() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399244",
                                        "branchCode": "001",
                                        "items": [
                                        {
                                        "productCode": "P001",
                                        "quantity": 3,
                                        "unitPrice": 5.20,
                                        "destination": "STOCK"
                                        },
                                        {
                                        "productCode": "P001",
                                        "quantity": 2,
                                        "unitPrice": 5,
                                        "destination": "STOCK"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.processed").value(false))
                .andExpect(jsonPath("$.items[0].productCode").value("P001"))
                .andExpect(jsonPath("$.totalValue").value(25.6));
    }

    @Test
    void shouldCreateInvoiceWhenDirectItemIsNotLinkedToBranch() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "001",
                                        "items":
                                        [
                                        {
                                        "productCode": "p002",
                                        "quantity": 3,
                                        "unitPrice": 20,
                                        "destination": "DIRECT"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.items[0].productCode").value("P002"))
                .andExpect(jsonPath("$.items[0].destination").value("DIRECT"));
    }

    @Test
    void shouldReturnNotFoundWhenStockItemIsNotLinkedToBranch() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "001",
                                        "items":
                                        [
                                        {
                                        "productCode": "p002",
                                        "quantity": 3,
                                        "unitPrice": 20,
                                        "destination": "STOCK"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("O produto 'P002' não está vinculado à filial '001'."));
    }

    @Test
    void shouldReturnConflictWhenInvoiceNumberAlreadyExistsForSupplier() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "001",
                                        "items":
                                        [
                                        {
                                        "productCode": "p002",
                                        "quantity": 3,
                                        "unitPrice": 20,
                                        "destination": "DIRECT"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "001",
                                        "items":
                                        [
                                        {
                                        "productCode": "p002",
                                        "quantity": 3,
                                        "unitPrice": 20,
                                        "destination": "DIRECT"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe uma nota fiscal com o número '000399444' para o fornecedor '0303804400ED01'."));
    }

    @Test
    void shouldReturnNotFoundWhenSupplierDoesNotExist() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "11111111111111")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "001",
                                        "items":
                                        [
                                        {
                                        "productCode": "p002",
                                        "quantity": 3,
                                        "unitPrice": 20,
                                        "destination": "DIRECT"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum fornecedor encontrado com o CNPJ '11111111111111'."));
    }

    @Test
    void shouldReturnNotFoundWhenBranchDoesNotExist() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "999",
                                        "items":
                                        [
                                        {
                                        "productCode": "p002",
                                        "quantity": 3,
                                        "unitPrice": 20,
                                        "destination": "DIRECT"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhuma filial encontrada com o código '999'."));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "001",
                                        "items":
                                        [
                                        {
                                        "productCode": "999",
                                        "quantity": 3,
                                        "unitPrice": 20,
                                        "destination": "DIRECT"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum produto encontrado com o código '999'."));
    }

    @Test
    void shouldReturnBadRequestWhenItemsListIsEmpty() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "001",
                                        "items": []
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.items").value("A nota deve ter pelo menos um item."));
    }

    @Test
    void shouldReturnBadRequestWhenItemQuantityIsZero() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "001",
                                        "items":
                                        [
                                        {
                                        "productCode": "P001",
                                        "quantity": 0,
                                        "unitPrice": 20,
                                        "destination": "STOCK"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['items[0].quantity']").value("A quantidade deve ser maior que zero."));
    }

    @Test
    void shouldReturnBadRequestWhenItemQuantityIsNull() throws Exception {
        mockMvc.perform(post("/suppliers/{cnpj}/invoices", "03.038.04400ed01")
                        .content(
                                """
                                        {
                                        "number": "000399444",
                                        "branchCode": "001",
                                        "items":
                                        [
                                        {
                                        "productCode": "P001",
                                        "quantity": null,
                                        "unitPrice": 20,
                                        "destination": "STOCK"
                                        }
                                        ]
                                        }
                                        """
                        ).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors['items[0].quantity']").value("A quantidade é obrigatória."));
    }
}