package gerenciar.cadastro.dto.response;

import java.time.Instant;

public record ClienteRespostaContatoDTO(Long id, String telefone, String email,
                                        Instant createdAt, Instant updatedAt) {
}
