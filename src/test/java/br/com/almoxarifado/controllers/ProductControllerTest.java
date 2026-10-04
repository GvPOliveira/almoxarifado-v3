package br.com.almoxarifado.controllers;


import br.com.almoxarifado.entities.Product;
import br.com.almoxarifado.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldCreateProduct() throws Exception {
        mockMvc.perform(
                post("/products").content("""
                        {
                            "code": "P001",
                            "name": "Product test"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andExpect(jsonPath("$.code").value("P001")
        ).andExpect(jsonPath("$.name").value("Product test"));

    }

    @Test
    void shouldReturnConflictWhenProductCodeAlreadyExists() throws Exception {
        mockMvc.perform(
                post("/products").content("""
                        {
                            "code": "P001",
                            "name": "Product test"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                post("/products").content("""
                        {
                            "code": "P001",
                            "name": "Product test"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Esse código já existe."));

    }


    @Test
    void getProducts() throws Exception {
        mockMvc.perform(
                post("/products").content("""
                        {
                            "code": "P1",
                            "name": "Product test"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/products").content("""
                        {
                            "code": "P2",
                            "name": "Product test2"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(
                post("/products").content("""
                        {
                            "code": "P3",
                            "name": "Product test3"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)
        );

        mockMvc.perform(get("/products")).andExpect(status().isOk()).andExpect(jsonPath("$[0].code").value("P1"))
                .andExpect(jsonPath("$[1].code").value("P2")).andExpect(jsonPath("$[2].code").value("P3"))
                .andExpect(jsonPath("$.length()").value(3));
    }


    @Test
    void getProductByCode() throws Exception {
        mockMvc.perform(post("/products").content("""
                {
                "code": "P200",
                "name": "ProdutoTest"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/products/P200")).andExpect(status().isOk()).andExpect(
                jsonPath("$.name").value("ProdutoTest")
        );

    }

    @Test
    void getProductByCodeNotFoundException() throws Exception {
        mockMvc.perform(get("/products/P200")).andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Esse código não existe."));
    }

}
