package gerenciar.cadastro.unit.service;

import gerenciar.cadastro.dto.request.ContatoAtualizacaoDTO;
import gerenciar.cadastro.dto.request.ContatoRequisicaoDto;
import gerenciar.cadastro.dto.response.ContatoRespostaDto;
import gerenciar.cadastro.dto.common.PageResponseDTO;
import gerenciar.cadastro.exception.ResourceNotFoundException;
import gerenciar.cadastro.loggin.logger.BusinessEventLogger;
import gerenciar.cadastro.loggin.logger.ErrorLogger;
import gerenciar.cadastro.loggin.logger.PerformanceLogger;
import gerenciar.cadastro.mapper.ContatoMapper;
import gerenciar.cadastro.model.entity.Cliente;
import gerenciar.cadastro.model.entity.Contato;
import gerenciar.cadastro.repository.ClienteRepository;
import gerenciar.cadastro.repository.ContatoRepository;
import gerenciar.cadastro.service.ContatoService;
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

class ContatoServiceTest {

    @Mock
    ClienteRepository clienteRepository;

    @Mock
    ContatoMapper contatoMapper;

    @Mock
    ContatoRepository contatoRepository;

    @Mock
    private BusinessEventLogger businessEventLogger;

    @Mock
    private PerformanceLogger performanceLogger;

    @Mock
    private ErrorLogger errorLogger;

    @Autowired
    @InjectMocks
    ContatoService contatoService;

    private Cliente cliente;
    private Contato contato;
    private ContatoRequisicaoDto requisicaoDto;
    private ContatoRespostaDto respostaDto;
    private ContatoAtualizacaoDTO atualizacaoDTO;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
        contato =  new Contato(1L,"999999999","email@test.com",cliente);
        cliente=  new Cliente(1L,"ana","39687547820",List.of(contato));

        requisicaoDto = new ContatoRequisicaoDto(1L,"999999999","email@test.com");
        respostaDto =new ContatoRespostaDto(1L, "999999999", "email@test.com", Instant.now(), Instant.now(), 1L);
        atualizacaoDTO = new ContatoAtualizacaoDTO("888888888", "novo@email.com");

    }


    @Test
    @DisplayName(" CadastarContato_EmailDisponivel_DeveCadastrarComSucesso")
    void CadastarContato_EmailDisponivel_DeveCadastrarComSucesso() {

        when(contatoRepository.existsByemail("email@test.com")).thenReturn(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(contatoMapper.paraEntidade(requisicaoDto)).thenReturn(contato);
        when(contatoRepository.save(contato)).thenReturn(contato);
        when(contatoMapper.paraRespostaDto(contato)).thenReturn(respostaDto);

        ContatoRespostaDto result = contatoService.CadastarContato(requisicaoDto);

        assertNotNull(result);
        assertEquals(1L, result.id());

        verify(contatoRepository).save(contato);
        verify(businessEventLogger).logEvent(any(), any(), any(), any());
        verify(performanceLogger).logExecution(any(), anyLong());
    }

    @Test
    @DisplayName(" CadastarContato_EmailJaExistente_DeveLancarExcecao ")
    void CadastarContato_EmailJaExistente_DeveLancarExcecao() {

        when(contatoRepository.existsByemail("email@test.com")).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class,
                () -> contatoService.CadastarContato(requisicaoDto));

        verify(errorLogger).logError(eq("409"), any(), isNull());
        verify(contatoRepository, never()).save(any());
    }

    @Test
    @DisplayName(" CadastarContato_ClienteNaoEncontrado_DeveLancarExcecao")
    void CadastarContato_ClienteNaoEncontrado_DeveLancarExcecao() {

        when(contatoRepository.existsByemail("email@test.com")).thenReturn(false);
        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contatoService.CadastarContato(requisicaoDto));
    }


    @Test
    @DisplayName(" listarContatosByclienteId_ClienteExistente_DeveRetornarLista")
    void listarContatosByclienteId_ClienteExistente_DeveRetornarLista() {

        cliente.setContatos(List.of(contato));
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        List<ContatoRespostaDto> result = contatoService.listarContatosByclienteId(1L);

        assertEquals(1, result.size());
        verify(businessEventLogger).logEvent(any(), any(), any(), any());
    }

    @Test
    @DisplayName(" listarContatosByclienteId_ClienteNaoEncontrado_DeveLancarExcecao ")
    void listarContatosByclienteId_ClienteNaoEncontrado_DeveLancarExcecao() {

        when(clienteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contatoService.listarContatosByclienteId(1L));
    }

    @Test
    @DisplayName(" RemoverContato_ContatoExistente_DeveRemoverComSucesso ")
    void RemoverContato_ContatoExistente_DeveRemoverComSucesso() {

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));

        contatoService.RemoverContato(1L);

        verify(contatoRepository).delete(contato);
        verify(businessEventLogger).logEvent(any(), any(), any(), any());
    }

    @Test
    @DisplayName(" RemoverContato_ContatoNaoEncontrado_DeveLancarExcecao ")
    void RemoverContato_ContatoNaoEncontrado_DeveLancarExcecao() {

        when(contatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contatoService.RemoverContato(1L));

        verify(contatoRepository, never()).delete(any());
    }

    @Test
    @DisplayName(" AtualizarContato_EmailDisponivel_DeveAtualizarComSucesso ")
    void AtualizarContato_EmailDisponivel_DeveAtualizarComSucesso() {

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(contatoRepository.existsByemail("novo@email.com")).thenReturn(false);
        when(contatoRepository.save(contato)).thenReturn(contato);
        when(contatoMapper.paraRespostaDto(contato)).thenReturn(respostaDto);

        ContatoRespostaDto result = contatoService.AtualizarContato(1L, atualizacaoDTO);

        assertNotNull(result);
        verify(contatoRepository).save(contato);
        verify(businessEventLogger).logEvent(any(), any(), any(), any());
    }

    @Test
    @DisplayName(" AtualizarContato_EmailJaExistente_DeveLancarExcecao ")
    void AtualizarContato_EmailJaExistente_DeveLancarExcecao() {

        when(contatoRepository.findById(1L)).thenReturn(Optional.of(contato));
        when(contatoRepository.existsByemail("novo@email.com")).thenReturn(true);

        assertThrows(DataIntegrityViolationException.class,
                () -> contatoService.AtualizarContato(1L, atualizacaoDTO));

        verify(errorLogger).logError(eq("409"), any(), isNull());
    }

    @Test
    @DisplayName(" AtualizarContato_ContatoNaoEncontrado_DeveLancarExcecao ")
    void AtualizarContato_ContatoNaoEncontrado_DeveLancarExcecao() {

        when(contatoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> contatoService.AtualizarContato(1L, atualizacaoDTO));
    }


    @Test
    @DisplayName(" listarTodosContatosPaginado_DadosValidos_DeveRetornarPagina ")
    void listarTodosContatosPaginado_DadosValidos_DeveRetornarPagina() {

        Page<Contato> page = new PageImpl<>(List.of(contato));
        when(contatoRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(contatoMapper.paraRespostaDto(contato)).thenReturn(respostaDto);

        PageResponseDTO<ContatoRespostaDto> result =
                contatoService.listarTodosContatosPaginado(0, 10);

        assertEquals(1, result.content().size());
        verify(businessEventLogger).logEvent(any(), any(), any(), any());
    }

    @Test
    @DisplayName(" listarContatosPorEmail_DadosValidos_DeveRetornarPagina ")
    void listarContatosPorEmail_DadosValidos_DeveRetornarPagina() {

        Page<Contato> page = new PageImpl<>(List.of(contato));
        when(contatoRepository.findByEmailContainingIgnoreCase(eq("email"), any(Pageable.class)))
                .thenReturn(page);
        when(contatoMapper.paraRespostaDto(contato)).thenReturn(respostaDto);

        PageResponseDTO<ContatoRespostaDto> result =
                contatoService.listarContatosPorEmail("email", 0, 10);

        assertEquals(1, result.content().size());
    }
}