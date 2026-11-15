package gerenciar.cadastro.dto.response;

import java.time.Instant;
import java.util.List;

public record ClienteRespostaDto( Long id, String nome, String cpf,
                                  Instant createdAt,
                                  Instant updatedAt,
                                  List<ClienteRespostaContatoDTO>contatos) { }
