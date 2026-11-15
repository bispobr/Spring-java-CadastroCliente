package gerenciar.cadastro.controller;


import gerenciar.cadastro.config.security.TokenConfig;
import gerenciar.cadastro.dto.request.LoginRequest;
import gerenciar.cadastro.dto.request.RegisterUserRequest;
import gerenciar.cadastro.dto.response.LoginResponse;
import gerenciar.cadastro.dto.response.RegisterUserResponse;
import gerenciar.cadastro.loggin.logger.AuditLogger;
import gerenciar.cadastro.model.entity.User;
import gerenciar.cadastro.model.enums.Role;
import gerenciar.cadastro.repository.UserRepository;
import gerenciar.cadastro.response.ResponseApi;
import gerenciar.cadastro.response.ResponseBuilder;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Endpoints de autenticação e registro de usuários")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    @Autowired
    AuditLogger auditLogger;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, TokenConfig tokenConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenConfig = tokenConfig;
    }



    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Endpoint responsável por autenticar o usuário e retornar o token JWT")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Token obtido com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);

        auditLogger.logLogin(user.getId(), user.getEmail(),user.getRoles().toString());
        return ResponseBuilder.ok(new LoginResponse(token),"Login realizado com sucesso");
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário", description = "Endpoint responsável por realizar o registro de novos usuários")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Registro realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = RegisterUserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<RegisterUserResponse>> register(@Valid @RequestBody RegisterUserRequest request) {

        User newUser = new User();
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setEmail(request.email());
        newUser.setName(request.name());
        newUser.setRoles(request.role() != null? Set.of(request.role()) : Set.of(Role.ROLE_USER) );


        userRepository.save(newUser);

        return ResponseBuilder.created(new RegisterUserResponse(newUser.getName(), newUser.getEmail()),  "Usuário registrado com sucesso");
    }

}
