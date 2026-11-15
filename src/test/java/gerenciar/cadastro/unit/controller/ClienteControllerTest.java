package gerenciar.cadastro.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gerenciar.cadastro.controller.ClienteController;
import gerenciar.cadastro.dto.response.ClienteRespostaDto;
import gerenciar.cadastro.dto.common.PageResponseDTO;
import gerenciar.cadastro.exception.ResourceNotFoundException;
import gerenciar.cadastro.service.ClienteService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import java.time.Instant;
import java.util.List;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService service;

    @InjectMocks
    ClienteController clienteController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    private ClienteRespostaDto responseDto;

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(clienteController).build();
        responseDto = new ClienteRespostaDto(1L, "Brendo", "12345678900", Instant.now(),Instant.now(),null);
    }



    @Test
    @DisplayName(" cadastrarCliente_dadosValidos_retornaCreated")
    void cadastrarCliente_dadosValidos_retornaCreated() throws Exception {

        when(service.CadastrarClientes(any())).thenReturn(responseDto);

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
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nome").value("Brendo"))
                .andExpect(jsonPath("$.message").value("Cliente cadastrado com sucesso"));

    }

    @Test
    @DisplayName(" cadastrarCliente_cpfDuplicado_retornaConflict ")
    void cadastrarCliente_cpfDuplicado_retornaConflict() throws Exception {

        when(service.CadastrarClientes(any()))
                .thenThrow(new DataIntegrityViolationException("CPF já cadastrado"));

        String json = """
                {
                    "nome": "Brendo",
                    "cpf": "12345678900"
                }
                """;

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/clientes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false));
        });
    }

    @Test
    @DisplayName(" cadastrarCliente_bodyInvalido_retornaBadRequest ")
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

    @Test
    @DisplayName("atualizarDadosCliente_idValido_retornaOk ")
    void atualizarDadosCliente_idValido_retornaOk() throws Exception {

        when(service.update(eq(1L), any())).thenReturn(responseDto);

        String json = """
                {
                    "nome": "Novo Nome",
                    "cpf": "12345678900"
                }
                """;

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName(" atualizarDadosCliente_clienteNaoEncontrado_retornaNotFound ")
    void atualizarDadosCliente_clienteNaoEncontrado_retornaNotFound() throws Exception {

        when(service.update(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("não encontrado"));

        String json = """
                {
                    "nome": "Novo Nome"
                }
                """;

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(put("/clientes/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        });
    }

    @Test
    @DisplayName(" listar_parametrosValidos_retornaOk ")
    void listar_parametrosValidos_retornaOk() throws Exception {

        PageResponseDTO<ClienteRespostaDto> page =
                new PageResponseDTO<>(List.of(responseDto), 0, 10, 1L, 1, true);

        when(service.listarTodosClientesPaginado(0, 10)).thenReturn(page);

        mockMvc.perform(get("/clientes?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName(" listar_parametrosInvalidos_retornaBadRequest ")
    void listar_parametrosInvalidos_retornaBadRequest() throws Exception {

        mockMvc.perform(get("/clientes?page=abc&size=10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(" buscarPorNome_parametrosValidos_retornaOk ")
    void buscarPorNome_parametrosValidos_retornaOk() throws Exception {

        PageResponseDTO<ClienteRespostaDto> page =
                new PageResponseDTO<>(List.of(responseDto), 0, 10, 1L, 1, true);

        when(service.listarClientesPorNome(eq("123"), eq(0), eq(10)))
                .thenReturn(page);

        mockMvc.perform(get("/clientes/buscar?cpf=123&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("removerCliente_idValido_retornaNoContent ")
    void removerCliente_idValido_retornaNoContent() throws Exception {

        doNothing().when(service).RemoverCliente(1L);

        mockMvc.perform(delete("/clientes/remover/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName(" removerCliente_clienteNaoEncontrado_retornaNotFound ")
    void removerCliente_clienteNaoEncontrado_retornaNotFound() throws Exception {

        doThrow(new ResourceNotFoundException("não encontrado"))
                .when(service).RemoverCliente(99L);

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(delete("/clientes/remover/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(true));
        });
    }

}



