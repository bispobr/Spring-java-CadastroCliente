package gerenciar.cadastro.mapper;

import gerenciar.cadastro.dto.request.ClienteRequisicaoDto;
import gerenciar.cadastro.dto.response.ClienteRespostaDto;
import gerenciar.cadastro.model.entity.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "contatos",ignore = true)

    Cliente paraEntidade (ClienteRequisicaoDto dto);

    ClienteRespostaDto paraRespostaDTO (Cliente cliente);

    List<ClienteRespostaDto> paraListaRespoataDTO (List<Cliente> lista);
}
