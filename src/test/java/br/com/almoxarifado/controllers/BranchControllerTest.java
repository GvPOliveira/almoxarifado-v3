package br.com.almoxarifado.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class BranchControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void shouldCreateBranchWhenDataIsValid() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "001",
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andExpect(
                jsonPath("$.name").value("BranchSouth")
        ).andExpect(jsonPath("$.code").value("001"));
    }

    @Test
    void shouldReturnConflictWhenBranchCodeAlreadyExists() throws Exception {

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
                "$.message").value("Já existe uma filial com o código '001'."));
    }

    @Test
    void shouldReturnConflictWhenBranchNameAlreadyExists() throws Exception {
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
                "$.message").value("Já existe uma filial com o nome 'BranchSouth'."));
    }

    @Test
    void shouldReturnBadRequestWhenNameIsNull() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": " 001",
                "name": null
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCodeIsNull() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": null,
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "001",
                "name": "    "
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCodeIsBlank() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "      ",
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBranchWhenCodeExists() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "001",
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/branches/001")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("BranchSouth"));
    }

    @Test
    void shouldReturnNotFoundWhenBranchCodeDoesNotExist() throws Exception {
        mockMvc.perform(get("/branches/001")).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Nenhuma filial encontrada com o código '001'."));
    }

    @Test
    void shouldReturnAllBranches() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                "code": "001",
                "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post("/branches").content("""
                {
                "code": "002",
                "name": "BranchNorth"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/branches")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void shouldReturnConflictWhenBranchCodeDiffersOnlyByCaseOrSpaces() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                  "code": "S01",
                  "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(post("/branches").content("""
                        {
                          "code": " s01",
                          "name": "BranchNorth"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe uma filial com o código 'S01'."));
    }

    @Test
    void shouldReturnBranchWhenFindBranchCodeDiffersOnlyByCase() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                  "code": "S01",
                  "name": "BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/branches/s01")).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("BranchSouth"));
    }

    @Test
    void shouldReturnConflictWhenCreateBranchNameDiffersOnlyByCase() throws Exception {
        mockMvc.perform(post("/branches").content("""
                        {
                          "code": "S01",
                          "name": "BranchSouth"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("BranchSouth"));
        mockMvc.perform(post("/branches").content("""
                        {
                          "code": "d01",
                          "name": "BRANCHSOUTH"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe uma filial com o nome 'BRANCHSOUTH'."));

        mockMvc.perform(post("/branches").content("""
                        {
                          "code": "B01",
                          "name": "branchsouth"
                        }
                        """).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Já existe uma filial com o nome 'branchsouth'."));
    }


    @Test
    void shouldPersistNormalizedCodeAndNameWhenCreatingBranch() throws Exception {
        mockMvc.perform(post("/branches").content("""
                {
                  "code": " s01",
                  "name": " BranchSouth"
                }
                """).contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(get("/branches/S01")).andExpect(status().isOk()).andExpect(
                jsonPath("$.name").value("BranchSouth")
        ).andExpect(jsonPath("$.code").value("S01"));
    }

}
