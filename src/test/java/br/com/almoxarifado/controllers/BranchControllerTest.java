package br.com.almoxarifado.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BranchControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void createBranch() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "001",
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(
                jsonPath("name").value("BranchSouth")
        ).andExpect(jsonPath("code").value("001"));
    }

    @Test
    void createBranchWithCodeDuplicated() throws Exception {

        mockMvc.perform(post("/branches").content("""
                {
                "code": "001",
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post("/branches").content("""
                {
                "code": " 001",
                "name": "BranchWest"
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict()).andExpect(jsonPath(
                "$.message").value("Essa filial com esse código já existe."));
    }

    @Test
    void createBranchWithNameDuplicated() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "001",
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post("/branches").content("""
                {
                "code": "002",
                "name": " BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict()).andExpect(jsonPath(
                "$.message").value("Essa filial já existe com esse nome."));
    }

    @Test
    void createBranchWithNameNull() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": " 001",
                "name": null
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void createBranchWithCodeNull() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": null,
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    @Test
    void createBranchWithNameBlank() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "001",
                "name": "    "
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    @Test
    void createBranchWithCodeBlank() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "      ",
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

}
