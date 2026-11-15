package gerenciar.cadastro.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gerenciar.cadastro.controller.ContatoController;
import gerenciar.cadastro.dto.response.ContatoRespostaDto;
import gerenciar.cadastro.dto.common.PageResponseDTO;
import gerenciar.cadastro.exception.ResourceNotFoundException;
import gerenciar.cadastro.service.ContatoService;
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
class ContatoControllerTest {


    @Mock
    private ContatoService service;

    @InjectMocks
    ContatoController contatoController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    MockMvc mockMvc;

    private  ContatoRespostaDto responseDto;

    @BeforeEach
    void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(contatoController).build();
        responseDto = new ContatoRespostaDto(1L, "61999999999", "teste@email.com", Instant.now(), Instant.now(), 1L
        );
    }



    @Test
    @DisplayName(" CadastarContato_dadosValidos_retornaCreated ")
    void CadastarContato_dadosValidos_retornaCreated() throws Exception {

        when(service.CadastarContato(any())).thenReturn(responseDto);

        String json = """
                {
                    "telefone": "61999999999",
                    "email": "teste@email.com",
                    "clienteId": 1
                }
                """;

        mockMvc.perform(post("/contatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Contato cadastrado com sucesso"));
    }

    @Test
    @DisplayName(" CadastarContato_emailDuplicado_retornaConflict ")
    void CadastarContato_emailDuplicado_retornaConflict() throws Exception {

        when(service.CadastarContato(any()))
                .thenThrow(new DataIntegrityViolationException("email duplicado"));

        String json = """
                {
                    "telefone": "61999999999",
                    "email": "teste@email.com",
                    "clienteId": 1
                }
                """;

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(post("/contatos")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.success").value(false));
        });
    }

    @Test
    @DisplayName(" CadastarContato_bodyInvalido_retornaBadRequest ")
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

    @Test
    @DisplayName(" AtualizarContato_idValido_retornaOk ")
    void AtualizarContato_idValido_retornaOk() throws Exception {

        when(service.AtualizarContato(eq(1L), any())).thenReturn(responseDto);

        String json = """
                {
                    "telefone": "61888888888",
                    "email": "novo@email.com"
                }
                """;

        mockMvc.perform(put("/contatos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName(" AtualizarContato_contatoNaoEncontrado_retornaNotFound ")
    void AtualizarContato_contatoNaoEncontrado_retornaNotFound() throws Exception {

        when(service.AtualizarContato(eq(99L), any()))
                .thenThrow(new ResourceNotFoundException("não encontrado"));

        String json = """
                {
                    "telefone": "61888888888"
                }
                """;

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(put("/contatos/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        });
    }

    @Test
    @DisplayName(" listarTodosContatosPagindao_parametrosValidos_retornaOk ")
    void listarTodosContatosPagindao_parametrosValidos_retornaOk() throws Exception {

        PageResponseDTO<ContatoRespostaDto> page =
                new PageResponseDTO<>(List.of(responseDto), 0, 10, 1L, 1, true);

        when(service.listarTodosContatosPaginado(0, 10)).thenReturn(page);

        mockMvc.perform(get("/contatos?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }



    @Test
    @DisplayName(" listarTodosContatosPagindao_parametrosInvalidos_retornaBadRequest ")
    void listarTodosContatosPagindao_parametrosInvalidos_retornaBadRequest() throws Exception {

        mockMvc.perform(get("/contatos?page=abc&size=10"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName(" buscarPorEmail_parametrosValidos_retornaOk ")
    void buscarPorEmail_parametrosValidos_retornaOk() throws Exception {

        PageResponseDTO<ContatoRespostaDto> page =
                new PageResponseDTO<>(List.of(responseDto), 0, 10, 1L, 1, true);

        when(service.listarContatosPorEmail(eq("teste"), eq(0), eq(10)))
                .thenReturn(page);

        mockMvc.perform(get("/contatos/buscar?email=teste&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName(" buscarPorEmail_emailAusente_retornaBadRequest ")
    void buscarPorEmail_emailAusente_retornaBadRequest() throws Exception {

        mockMvc.perform(get("/contatos/buscar?page=0&size=10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName(" listarClientesByID_idValido_retornaOk ")
    void listarClientesByID_idValido_retornaOk() throws Exception {

        when(service.listarContatosByclienteId(1L))
                .thenReturn(List.of(responseDto));

        mockMvc.perform(get("/contatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName(" listarClientesByID_clienteNaoEncontrado_retornaNotFound ")
    void listarClientesByID_clienteNaoEncontrado_retornaNotFound() throws Exception {

        when(service.listarContatosByclienteId(99L))
                .thenThrow(new ResourceNotFoundException("não encontrado"));

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(get("/contatos/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false));
        });
    }

    @Test
    @DisplayName(" removerContato_idValido_retornaNoContent ")
    void removerContato_idValido_retornaNoContent() throws Exception {

        doNothing().when(service).RemoverContato(1L);

        mockMvc.perform(delete("/contatos/remover/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName(" removerContato_contatoNaoEncontrado_retornaNotFound ")
    void removerContato_contatoNaoEncontrado_retornaNotFound() throws Exception {

        doThrow(new ResourceNotFoundException("não encontrado"))
                .when(service).RemoverContato(99L);

        assertThrows(ServletException.class, () -> {
            mockMvc.perform(delete("/contatos/remover/99"))
                    .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(true));
        });
    }


}