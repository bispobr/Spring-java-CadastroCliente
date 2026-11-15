package gerenciar.cadastro.dto.request;

import jakarta.validation.constraints.Email;

public record ContatoAtualizacaoDTO(String telefone, @Email String email) {
}
