package br.com.almoxarifado.controllers;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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


}
