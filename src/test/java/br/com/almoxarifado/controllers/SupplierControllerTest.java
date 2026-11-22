package br.com.almoxarifado.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class SupplierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateSupplierWhenDataIsValid() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": " Casas Bahia ",
                        "cnpj": "03.038.044/00ed-01"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Casas Bahia"))
                .andExpect(jsonPath("$.cnpj").value("0303804400ED01"));
    }

    @Test
    void shouldCreateSupplierWhenNameAlreadyExistsButCnpjIsDifferent() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": "Casas Bahia",
                        "cnpj": "03.038.044/00ed-01"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": "Casas Bahia",
                        "cnpj": "03.038.044/00ed-02"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Casas Bahia"))
                .andExpect(jsonPath("$.cnpj").value("0303804400ED02"));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsNull() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": null,
                        "cnpj": "03.038.044/00ed-01"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").value("O nome é obrigatório."));
    }

    @Test
    void shouldReturnBadRequestWhenCnpjIsNull() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": "Casas Bahia",
                        "cnpj": null
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.cnpj").value("O CNPJ é obrigatório."));
    }

    @Test
    void shouldReturnBadRequestWhenCnpjIsInvalid() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": "Casas Bahia",
                        "cnpj": "..."
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O CNPJ do fornecedor deve ter 14 caracteres."));
    }

    @Test
    void shouldReturnConflictWhenSupplierAlreadyExists() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                {
                "name": "Casas Bahia",
                "cnpj": "03.038.044/00ed-01"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": "Casas Bahia",
                        "cnpj": "0303804400ED01"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe um fornecedor com o CNPJ '0303804400ED01'."));
    }

    @Test
    void shouldReturnBadRequestWhenCnpjHasFewerThan14Characters() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": "Casas Bahia",
                        "cnpj": "1320938401112"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O CNPJ do fornecedor deve ter 14 caracteres."));
    }

    @Test
    void shouldReturnBadRequestWhenCnpjHasMoreThan14Characters() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": "Casas Bahia",
                        "cnpj": "132093840111211"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O CNPJ do fornecedor deve ter 14 caracteres."));
    }

    @Test
    void shouldReturnSupplierWhenCnpjExists() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": " Casas Bahia ",
                        "cnpj": "03.038.044/00ED-01"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/suppliers/0303804400ed01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Casas Bahia"))
                .andExpect(jsonPath("$.cnpj").value("0303804400ED01"));
    }

    @Test
    void shouldReturnNotFoundWhenSupplierDoesNotExist() throws Exception {
        mockMvc.perform(get("/suppliers/0303804400ed01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhum fornecedor encontrado com o CNPJ '0303804400ED01'."));
    }

    @Test
    void shouldReturnAllSuppliersWhenSuppliersExist() throws Exception {
        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": " Casas Bahia ",
                        "cnpj": "03.038.044/00ed-01"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/suppliers").content("""
                        {
                        "name": " Casas Bahia ",
                        "cnpj": "03.038.044/00ed-02"
                        }
                        """).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[1].cnpj").value("0303804400ED02"));
    }

    @Test
    void shouldReturnEmptyListWhenNoSuppliersExist() throws Exception {
        mockMvc.perform(get("/suppliers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
