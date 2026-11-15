package gerenciar.cadastro.service;

import gerenciar.cadastro.dto.request.ClienteAtualizacaoDTO;
import gerenciar.cadastro.dto.request.ClienteRequisicaoDto;
import gerenciar.cadastro.dto.response.ClienteRespostaDto;
import gerenciar.cadastro.dto.common.PageResponseDTO;
import gerenciar.cadastro.exception.ResourceNotFoundException;
import gerenciar.cadastro.loggin.logger.BusinessEventLogger;
import gerenciar.cadastro.loggin.logger.ErrorLogger;
import gerenciar.cadastro.loggin.logger.PerformanceLogger;
import gerenciar.cadastro.mapper.ClienteMapper;
import gerenciar.cadastro.model.entity.Cliente;
import gerenciar.cadastro.repository.ClienteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
public class ClienteService {

    @Autowired
    ClienteRepository clienteRepository;

    @Autowired
    ClienteMapper clienteMapper;

    @Autowired
    BusinessEventLogger businessEventLogger;

    @Autowired
    PerformanceLogger performanceLogger;

    @Autowired
    ErrorLogger errorLogger;

    @Transactional
    public ClienteRespostaDto CadastrarClientes(ClienteRequisicaoDto dto){
        long start = System.currentTimeMillis();

        if (!clienteRepository.existsBycpf(dto.cpf())) {
            Cliente novoCadastro = clienteRepository.save(clienteMapper.paraEntidade(dto));

            logs("Cliente_Cadastrado","Cliente","SUCESSO","Cliente_Cadastrado",novoCadastro.getId(),start);

            return clienteMapper.paraRespostaDTO(novoCadastro);
        }
        errorLogger.logError("409","CPF ja cadastrado",null);
        throw  new DataIntegrityViolationException(null);
    }

    @Transactional(readOnly = true)
    public PageResponseDTO<ClienteRespostaDto> listarClientesPorNome(String cpf, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ClienteRespostaDto> resultado = clienteRepository.findByCpfContainingIgnoreCase(cpf, pageable)
                .map(clienteMapper::paraRespostaDTO);

        return PageResponseDTO.of(resultado);
    }


    @Transactional(readOnly = true)
    public PageResponseDTO<ClienteRespostaDto> listarTodosClientesPaginado(int page, int size) {
        long start = System.currentTimeMillis();

        Page<Cliente> pageResult = clienteRepository.findAll(PageRequest.of(page, size, Sort.by("id")));
        Page<ClienteRespostaDto> dtoPage = pageResult.map(clienteMapper::paraRespostaDTO);

        logs("Listagem_clientes_Pagindao","Cliente","SUCESSO","Listagem_cliente_Pagindao",start);

        return PageResponseDTO.of(dtoPage);
    }


    @Transactional
    public ClienteRespostaDto update(Long id, ClienteAtualizacaoDTO dto) {
        long start = System.currentTimeMillis();

        Cliente cliente = clienteRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cliente id:"+id+" não Encontrado "));

        if (!clienteRepository.existsBycpf(dto.cpf())) {
            cliente.setNome(dto.nome() != null ? dto.nome(): cliente.getNome());
            cliente.setCpf(dto.cpf() != null ? dto.cpf(): cliente.getCpf());

            logs("Cliente_Atualização","Cliente","SUCESSO","Cliente_Atualização",id,start);

            return  clienteMapper.paraRespostaDTO(clienteRepository.save(cliente));
        }

        errorLogger.logError("409","CPF ja cadastrado",null);
        throw  new DataIntegrityViolationException(null);

    }

    @Transactional
    public void RemoverCliente(Long id) {

        long start = System.currentTimeMillis();

        Cliente cliente = clienteRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Cliente id:"+id+" não Encontrado "));

        clienteRepository.delete(cliente);

        logs("Cliente_remoção","Cliente","SUCESSO","Cliente_remoção",id,start);
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
