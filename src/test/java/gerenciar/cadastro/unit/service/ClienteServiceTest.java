package gerenciar.cadastro.unit.service;

import gerenciar.cadastro.dto.common.PageResponseDTO;
import gerenciar.cadastro.dto.request.ClienteAtualizacaoDTO;
import gerenciar.cadastro.dto.request.ClienteRequisicaoDto;
import gerenciar.cadastro.dto.response.ClienteRespostaDto;
import gerenciar.cadastro.exception.ResourceNotFoundException;
import gerenciar.cadastro.loggin.logger.BusinessEventLogger;
import gerenciar.cadastro.loggin.logger.ErrorLogger;
import gerenciar.cadastro.loggin.logger.PerformanceLogger;
import gerenciar.cadastro.mapper.ClienteMapper;
import gerenciar.cadastro.model.entity.Cliente;
import gerenciar.cadastro.repository.ClienteRepository;
import gerenciar.cadastro.service.ClienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ClienteMapper clienteMapper;

    @Mock
    private BusinessEventLogger businessEventLogger;

    @Mock
    private PerformanceLogger performanceLogger;

    @Mock
    private ErrorLogger errorLogger;

    @Autowired
    @InjectMocks
    private ClienteService clienteService;


    private Cliente novoCadastro;
    private ClienteRequisicaoDto requisicaoDto;
    private ClienteRespostaDto respostaDto;
    private ClienteAtualizacaoDTO atualizacaoDTO;


    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        novoCadastro = new Cliente(1L,"João","12345678900",null);
        requisicaoDto =  new ClienteRequisicaoDto("João", "12345678900");
        respostaDto = new ClienteRespostaDto(1L,"João","12345678900",Instant.now(),Instant.now(),null);
        atualizacaoDTO = new ClienteAtualizacaoDTO("Maria", "98765432122");;
    }



    @Test
    @DisplayName("CadastrarClientes_CpfNaoExistente_DeveCadastrarComSucesso")
    void CadastrarClientes_CpfNaoExistente_DeveCadastrarComSucesso() {


        when(clienteRepository.existsBycpf("12345678900")).thenReturn(false);
        when(clienteMapper.paraEntidade(requisicaoDto)).thenReturn(novoCadastro);
        when(clienteRepository.save(novoCadastro)).thenReturn(novoCadastro);
        when(clienteMapper.paraRespostaDTO(novoCadastro)).thenReturn(respostaDto);

        ClienteRespostaDto resultado = clienteService.CadastrarClientes(requisicaoDto);

        assertNotNull(resultado);
        assertEquals("João", resultado.nome());
        assertEquals(1L, resultado.id());

        verify(clienteRepository).save(novoCadastro);
        verify(businessEventLogger).logEvent(any(), any(), any(), any());
        verify(performanceLogger).logExecution(any(), anyLong());
    }


    @Test
    @DisplayName("CadastrarClientes_CpfJaExistente_DeveLancarExcecao")
    void CadastrarClientes_CpfJaExistente_DeveLancarExcecao() {
        //ClienteRequisicaoDto dto = new ClienteRequisicaoDto("João", "12345678900");

        when(clienteRepository.existsBycpf("12345678900")).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class, () -> clienteService.CadastrarClientes(requisicaoDto));
        verify(errorLogger).logError(eq("409"), any(), isNull());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("listarClientesPorNome_DadosValidos_DeveRetornarPagina")
    void listarClientesPorNome_DadosValidos_DeveRetornarPagina() {

        Page<Cliente> page = new PageImpl<>(List.of(novoCadastro));
        when(clienteRepository.findByCpfContainingIgnoreCase(eq("12345678900"), any(Pageable.class)))
                .thenReturn(page);
        when(clienteMapper.paraRespostaDTO(novoCadastro)).thenReturn(respostaDto);

        PageResponseDTO<ClienteRespostaDto> result =
                clienteService.listarClientesPorNome("12345678900", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());
    }


    @Test
    @DisplayName("listarTodosClientesPaginado_DadosValidos_DeveRetornarPagina")
    void listarTodosClientesPaginado_DadosValidos_DeveRetornarPagina() {

        Page<Cliente> page = new PageImpl<>(List.of(novoCadastro));

        when(clienteRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(clienteMapper.paraRespostaDTO(novoCadastro)).thenReturn(respostaDto);

        PageResponseDTO<ClienteRespostaDto> result =
                clienteService.listarTodosClientesPaginado(0, 10);

        assertNotNull(result);
        assertEquals(1, result.content().size());

        verify(businessEventLogger).logEvent(any(), any(), any(), any());
        verify(performanceLogger).logExecution(any(), anyLong());
    }

    @Test
    @DisplayName("update_ClienteExistenteCpfDisponivel_DeveAtualizarComSucesso")
    void update_ClienteExistenteCpfDisponivel_DeveAtualizarComSucesso() {

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(novoCadastro));
        when(clienteRepository.existsBycpf("98765432122")).thenReturn(false);
        when(clienteRepository.save(novoCadastro)).thenReturn(novoCadastro);
        when(clienteMapper.paraRespostaDTO(novoCadastro)).thenReturn(respostaDto);

        ClienteRespostaDto result = clienteService.update(1L, atualizacaoDTO);

        assertNotNull(result);
        verify(clienteRepository).save(novoCadastro);
        verify(businessEventLogger).logEvent(any(), any(), any(), any());
    }

    @Test
    @DisplayName("update_ClienteNaoEncontrado_DeveLancarExcecao")
    void update_ClienteNaoEncontrado_DeveLancarExcecao() {

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.update(1L, atualizacaoDTO));

        verify(clienteRepository, never()).save(any());
    }


    @Test
    @DisplayName("update_CpfJaExistente_DeveLancarExcecao")
    void update_CpfJaExistente_DeveLancarExcecao() {

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(novoCadastro));
        when(clienteRepository.existsBycpf("98765432122")).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class,
                () -> clienteService.update(1L, atualizacaoDTO));

        verify(errorLogger).logError(eq("409"), any(), isNull());
        verify(clienteRepository, never()).save(any());
    }

    @Test
    @DisplayName(" RemoverCliente_ClienteExistente_DeveRemoverComSucesso")
    void RemoverCliente_ClienteExistente_DeveRemoverComSucesso() {

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(novoCadastro));

        clienteService.RemoverCliente(1L);

        verify(clienteRepository).delete(novoCadastro);
        verify(businessEventLogger).logEvent(any(), any(), any(), any());
        verify(performanceLogger).logExecution(any(), anyLong());
    }

    @Test
    @DisplayName("RemoverCliente_ClienteNaoEncontrado_DeveLancarExcecao ")
    void RemoverCliente_ClienteNaoEncontrado_DeveLancarExcecao() {

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.RemoverCliente(1L));

        verify(clienteRepository, never()).delete(any());
    }

}