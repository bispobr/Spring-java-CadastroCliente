package gerenciar.cadastro.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ContatoRequisicaoDto(@NotNull Long clienteId,@NotBlank String telefone, @NotBlank @Email String email ) { }
