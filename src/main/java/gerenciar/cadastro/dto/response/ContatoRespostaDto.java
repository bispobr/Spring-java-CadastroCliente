package gerenciar.cadastro.dto.response;

import java.time.Instant;


public record ContatoRespostaDto(Long id, String telefone, String email,
                                 Instant createdAt, Instant updatedAt,
                                 Long clienteId) { }
