package gerenciar.cadastro.integration.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import gerenciar.cadastro.model.entity.Cliente;
import gerenciar.cadastro.model.entity.Contato;
import gerenciar.cadastro.repository.ClienteRepository;
import gerenciar.cadastro.repository.ContatoRepository;
import jakarta.transaction.Transactional;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;



@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ContatoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContatoRepository contatoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente;

    @BeforeEach
    void setup() {
        contatoRepository.deleteAll();
        clienteRepository.deleteAll();

        cliente = new Cliente();
        cliente.setNome("Cliente Teste");
        cliente.setCpf("12345678900");
        cliente = clienteRepository.save(cliente);
    }

    // =============================
    // CENÁRIO CRÍTICO 1
    // =============================
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("CadastarContato_dadosValidos_retornaCreated")
    void CadastarContato_dadosValidos_retornaCreated() throws Exception {

        String json = """
                {
                    "telefone": "61999999999",
                    "email": "teste@email.com",
                    "clienteId": %d
                }
                """.formatted(cliente.getId());

        mockMvc.perform(post("/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    // =============================
    // CENÁRIO CRÍTICO 2
    // =============================
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("CadastarContato_emailDuplicado_retornaConflict")
    void CadastarContato_emailDuplicado_retornaConflict() throws Exception {

        Contato contato = new Contato();
        contato.setEmail("teste@email.com");
        contato.setTelefone("61999999999");
        contato.setCliente(cliente);
        contatoRepository.save(contato);

        String json = """
                {
                    "telefone": "61999999999",
                    "email": "teste@email.com",
                    "clienteId": %d
                }
                """.formatted(cliente.getId());

        mockMvc.perform(post("/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }

    // =============================
    // CENÁRIO CRÍTICO 3
    // =============================
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("CadastarContato_bodyInvalido_retornaBadRequest")
    void CadastarContato_bodyInvalido_retornaBadRequest() throws Exception {

        String json = """
                {
                    "email": "invalido"
                }
                """;

        mockMvc.perform(post("/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // =============================
    // CENÁRIO CRÍTICO 4
    // =============================
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("AtualizarContato_contatoNaoEncontrado_retornaNotFound")
    void AtualizarContato_contatoNaoEncontrado_retornaNotFound() throws Exception {

        String json = """
                {
                    "telefone": "61888888888"
                }
                """;

        mockMvc.perform(put("/contatos/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());
    }

    // =============================
    // CENÁRIO CRÍTICO 5
    // =============================
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("buscarPorEmail_parametrosValidos_retornaOk")
    void buscarPorEmail_parametrosValidos_retornaOk() throws Exception {

        Contato contato = new Contato();
        contato.setEmail("teste@email.com");
        contato.setTelefone("61999999999");
        contato.setCliente(cliente);
        contatoRepository.save(contato);

        mockMvc.perform(get("/contatos/buscar?email=teste&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // =============================
    // CENÁRIO CRÍTICO 6
    // =============================
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("buscarPorEmail_emailAusente_retornaBadRequest")
    void buscarPorEmail_emailAusente_retornaBadRequest() throws Exception {

        mockMvc.perform(get("/contatos/buscar?page=0&size=10"))
                .andExpect(status().isBadRequest());
    }

    // =============================
    // CENÁRIO CRÍTICO 7
    // =============================
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Test
    @DisplayName("removerContato_contatoNaoEncontrado_retornaNotFound")
    void removerContato_contatoNaoEncontrado_retornaNotFound() throws Exception {

        mockMvc.perform(delete("/contatos/remover/999"))
                .andExpect(status().isNotFound());
    }
}