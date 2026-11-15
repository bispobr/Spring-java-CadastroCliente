package gerenciar.cadastro.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ClienteRequisicaoDto(@NotBlank String nome,@NotBlank String cpf) { }
