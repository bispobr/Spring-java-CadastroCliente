package gerenciar.cadastro.mapper;

import gerenciar.cadastro.dto.request.ContatoRequisicaoDto;
import gerenciar.cadastro.dto.response.ContatoRespostaDto;
import gerenciar.cadastro.model.entity.Contato;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContatoMapper {

    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id",ignore = true)
    @Mapping(target = "cliente",ignore = true)

    Contato paraEntidade (ContatoRequisicaoDto dto);

    @Mapping(target = "clienteId",source = "cliente.id")
    ContatoRespostaDto paraRespostaDto (Contato contado);


    List<ContatoRespostaDto> paraListaRespostaDTO (List<Contato> lista);
}
