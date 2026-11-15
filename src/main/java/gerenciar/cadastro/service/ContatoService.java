package gerenciar.cadastro.service;

import gerenciar.cadastro.dto.common.PageResponseDTO;
import gerenciar.cadastro.dto.request.ContatoAtualizacaoDTO;
import gerenciar.cadastro.dto.request.ContatoRequisicaoDto;
import gerenciar.cadastro.dto.response.ContatoRespostaDto;
import gerenciar.cadastro.exception.ResourceNotFoundException;
import gerenciar.cadastro.loggin.logger.BusinessEventLogger;
import gerenciar.cadastro.loggin.logger.ErrorLogger;
import gerenciar.cadastro.loggin.logger.PerformanceLogger;
import gerenciar.cadastro.mapper.ContatoMapper;
import gerenciar.cadastro.model.entity.Cliente;
import gerenciar.cadastro.model.entity.Contato;
import gerenciar.cadastro.repository.ClienteRepository;
import gerenciar.cadastro.repository.ContatoRepository;
import org.springframework.cache.annotation.CachePut;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContatoService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ContatoMapper contatoMapper;

    @Autowired
    ContatoRepository contatoRepository;

    @Autowired
    BusinessEventLogger businessEventLogger;

    @Autowired
    PerformanceLogger performanceLogger;

    @Autowired
    ErrorLogger errorLogger;


    @Transactional
    @CachePut(value = "contato", key = "#result.id")
    public ContatoRespostaDto  CadastarContato(ContatoRequisicaoDto dto){

        long start = System.currentTimeMillis();

        if (!contatoRepository.existsByemail(dto.email())) {
            Cliente cliente = clienteRepository.findById(dto.clienteId()).orElseThrow(() -> new ResourceNotFoundException("Cliente id:" + dto.clienteId() + " não Encontrado "));
            Contato contato = contatoMapper.paraEntidade(dto);
            contato.setCliente(cliente);
            contatoRepository.save(contato);

            logs("Contato_Cadastrado","Cliente","SUCESSO","Contato_Cadastrado", contato.getId(),start);

            return contatoMapper.paraRespostaDto(contato);
        }
        errorLogger.logError("409","email ja cadastrado",null);
        throw  new DataIntegrityViolationException(null);
    }

    @Transactional(readOnly = true)
    public  List<ContatoRespostaDto> listarContatosByclienteId(Long clienteId){

        long start = System.currentTimeMillis();

        Cliente cliente = clienteRepository.findById(clienteId).orElseThrow(()-> new ResourceNotFoundException("Cliente id:"+clienteId+" não Encontrado "));

        List<ContatoRespostaDto> Contatos = cliente.getContatos().stream().map(c ->{
            return new ContatoRespostaDto(c.getId(),c.getTelefone(),c.getEmail(),c.getCreatedAt(),c.getUpdatedAt(),cliente.getId());
        }).collect(Collectors.toList());

        logs("Listagem_contatos_cliente","Cliente","SUCESSO","Listagem_contatos_cliente",clienteId,start);


        return Contatos;


    }

    @Transactional
    public void RemoverContato(Long id) {

        long start = System.currentTimeMillis();

        Contato contato =   contatoRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Contado id:"+id+" não Encontrado "));
        contatoRepository.delete(contato);

        logs("Contato_remoção","Cliente","SUCESSO","Contato_remoção",id,start);
    }

    @Transactional

    public ContatoRespostaDto AtualizarContato(Long id, ContatoAtualizacaoDTO dto) {

        long start = System.currentTimeMillis();

        Contato contato = contatoRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Contado id:"+id+" não Encontrado "));

        if (!contatoRepository.existsByemail(dto.email())) {
            contato.setTelefone(dto.telefone() != null ? dto.telefone(): contato.getTelefone());
            contato.setEmail(dto.email() != null ? dto.email(): contato.getEmail());

            Contato contatoUpdate = contatoRepository.save(contato);

            logs("Contato_Atualização","Contato","SUCESSO","Contato_Atualização",id,start);

            return  contatoMapper.paraRespostaDto(contatoUpdate);
        }
        errorLogger.logError("409","email ja cadastrado",null);
        throw  new DataIntegrityViolationException(null);
    }


    @Transactional(readOnly = true)
    public PageResponseDTO<ContatoRespostaDto> listarTodosContatosPaginado(int page, int size) {
        long start = System.currentTimeMillis();

        Page<Contato> pageResult = contatoRepository.findAll(PageRequest.of(page, size, Sort.by("id")));
        Page<ContatoRespostaDto> dtoPage = pageResult.map(contatoMapper::paraRespostaDto);

        logs("Listagem_Contatos_Pagindao","Cliente","SUCESSO","Listagem_Contatos_Pagindao",start);

        return PageResponseDTO.of(dtoPage);
    }

    public PageResponseDTO<ContatoRespostaDto> listarContatosPorEmail(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ContatoRespostaDto> resultado = contatoRepository
                .findByEmailContainingIgnoreCase(email, pageable)
                .map(contatoMapper::paraRespostaDto);

        return PageResponseDTO.of(resultado);
    }


    public void logs(String evento, String entidade,String status,String operacao, long start){

        businessEventLogger.logEvent( evento, entidade, null, status);
        long duration = System.currentTimeMillis() - start;
        performanceLogger.logExecution("Listagem_Paginada",duration);

    }

    public void logs(String evento, String entidade,String status,String operacao,Long idEntidade, long start){

        businessEventLogger.logEvent( evento, entidade, idEntidade, status);
        long duration = System.currentTimeMillis() - start;
        performanceLogger.logExecution(operacao,duration);

    }

}