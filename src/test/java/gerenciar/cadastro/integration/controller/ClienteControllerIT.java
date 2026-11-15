package gerenciar.cadastro.integration.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import gerenciar.cadastro.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClienteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void cleanDatabase() {
        clienteRepository.deleteAll();
    }


    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("cadastrarCliente_dadosValidos_retornaCreated")
    void cadastrarCliente_dadosValidos_retornaCreated() throws Exception {

        String json = """
                {
                    "nome": "Brendo",
                    "cpf": "12345678900"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.nome").value("Brendo"));
    }


    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("cadastrarCliente_cpfDuplicado_retornaConflict")
    void cadastrarCliente_cpfDuplicado_retornaConflict() throws Exception {

        String jsonCliente = """
                {
                    "nome": "Brendo",
                    "cpf": "12345678900"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonCliente));



        String json = """
                {
                    "nome": "Outro",
                    "cpf": "12345678900"
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
    }


    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("cadastrarCliente_bodyInvalido_retornaBadRequest")
    void cadastrarCliente_bodyInvalido_retornaBadRequest() throws Exception {

        String json = """
                {
                    "nome": ""
                }
                """;

        mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }


    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("atualizarDadosCliente_clienteNaoEncontrado_retornaNotFound")
    void atualizarDadosCliente_clienteNaoEncontrado_retornaNotFound() throws Exception {

        String json = """
                {
                    "nome": "Novo Nome"
                }
                """;

        mockMvc.perform(put("/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }


    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("removerCliente_clienteNaoEncontrado_retornaNotFound")
    void removerCliente_clienteNaoEncontrado_retornaNotFound() throws Exception {

        mockMvc.perform(delete("/clientes/remover/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}