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

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BranchProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void createBranchAndProduct() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "001",
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(
                post("/products").content("""
                        {
                            "code": "P001",
                            "name": "Product test"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    void shouldCreateBranchProductWhenDataIsValid() throws Exception {
        mockMvc.perform(post("/branches/{branchCode}/products", "001").content("""
                         {
                            "productCode": "P001",
                            "location": "C001.P023.C.D01"
                         }
                        """).contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated()).andExpect(jsonPath("$.productCode").value("P001"))
                .andExpect(jsonPath("$.location").value("C001.P023.C.D01"))
                .andExpect(jsonPath("$.quantity").value(0))
                .andExpect(jsonPath("$.branchCode").value("001"));
    }

    @Test
    void shouldReturnConflictWhenBranchProductAlreadyExists() throws Exception {
        mockMvc.perform(post("/branches/{branchCode}/products", "001").content("""
                 {
                    "productCode": "P001",
                    "location": "C001.P023.C.D01"
                 }
                """).contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/branches/{branchCode}/products", "001").content("""
                         {
                            "productCode": "P001",
                            "location": ""
                         }
                        """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe um produto com esse código 'P001' nessa filial '001'."));
    }

    @Test
    void shouldCreateBranchProductWhenLocationIsBlank() throws Exception {
        mockMvc.perform(post("/branches/{branchCode}/products", "001").content("""
                 {
                    "productCode": "P001",
                    "location": "         "
                 }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(
                jsonPath("$.location", nullValue())

        );
    }

    @Test
    void shouldNormalizeLocationWhenCreatingBranchProduct() throws Exception {
        mockMvc.perform(post("/branches/{branchCode}/products", "001").content("""
                 {
                    "productCode": "P001",
                    "location": " c001.P03.d.c1 "
                 }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(
                jsonPath("$.location").value("C001.P03.D.C1")
        );
    }

    @Test
    void shouldReturnBadRequestWhenProductCodeIsBlank() throws Exception {
        mockMvc.perform(post("/branches/{branchCode}/products", "001").content("""
                         {
                            "productCode": "",
                            "location": "P023"
                         }
                        """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.productCode").value("O código do produto é obrigatório."));
    }

    @Test
    void shouldReturnBadRequestWhenLocationExceedsMaxLength() throws Exception {
        mockMvc.perform(post("/branches/{branchCode}/products", "001").content("""
                         {
                            "productCode": "P001",
                            "location": "C001.P023.D.D0122"
                         }
                        """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.location").value("A localização deve ter no máximo 15 caracteres."));
    }

    @Test
    void shouldReturnNotFoundWhenBranchDoesNotExist() throws Exception {
        mockMvc.perform(post("/branches/{branchCode}/products", "006").content("""
                         {
                            "productCode": "P001",
                            "location": "C001.PO23.D.D01"
                         }
                        """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhuma filial encontrada com o código '006'."));
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        mockMvc.perform(post("/branches/{branchCode}/products", "001").content("""
                         {
                            "productCode": "P023",
                            "location": "C001.PO23.D.D01"
                         }
                        """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum produto encontrado com o código 'P023'."));
    }

}
