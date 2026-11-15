package gerenciar.cadastro.dto.request;

import gerenciar.cadastro.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequest(@NotEmpty(message = "Nome é obrigatório") String name,
                                  @Email @NotEmpty(message = "E-mail é obrigatório") String email,
                                  @NotEmpty(message = "Senha é obrigatória") String password,
                                  Role role) {
}