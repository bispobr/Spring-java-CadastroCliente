package gerenciar.cadastro.controller;

import gerenciar.cadastro.dto.request.ClienteAtualizacaoDTO;
import gerenciar.cadastro.dto.request.ClienteRequisicaoDto;
import gerenciar.cadastro.dto.response.ClienteRespostaDto;
import gerenciar.cadastro.service.ClienteService;
import gerenciar.cadastro.response.PagedResponse;
import gerenciar.cadastro.response.ResponseApi;
import gerenciar.cadastro.response.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/clientes")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Clientes", description = "Endpoints para gerenciamento de clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;


    @PostMapping
    @Operation(summary = "Criar Cliente", description = "Endpoint responsável por criar um novo cliente no sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente salvo com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteRespostaDto.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "CPF já cadastrado",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<ClienteRespostaDto>> cadastrarCliente(@RequestBody @Valid ClienteRequisicaoDto clienteRequisicaoDto) {

        return ResponseBuilder.created(clienteService.CadastrarClientes(clienteRequisicaoDto), "Cliente cadastrado com sucesso");
    }



    @PutMapping("/{id}")
    @Operation(summary = "Atualizar cliente", description = "Endpoint responsável por atualizar dados do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteRespostaDto.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<ClienteRespostaDto>> atualizarDadosCliente(@PathVariable Long id, @RequestBody  ClienteAtualizacaoDTO dto) {

        return ResponseBuilder.ok(clienteService.update(id,dto),"Cliente atualizado com sucesso");
    }



    @GetMapping
    @Operation(summary = "Listar clientes", description = "Endpoint responsável por listar clientes e seus contatos com paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteRespostaDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public  ResponseEntity<ResponseApi<PagedResponse<ClienteRespostaDto>>> listar(
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {

        return ResponseBuilder.pagedFromDTO(clienteService.listarTodosClientesPaginado(page, size));
    }



    @GetMapping("/buscar")
    @Operation(summary = "Buscar Clientes", description = "Endpoint responsável por buscar clientes pelo cpf completo ou parcial com paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de clientes retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ClienteRespostaDto.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<PagedResponse<ClienteRespostaDto>>> buscarPorNome(@Parameter( required = true) @RequestParam String cpf,
                                                                                        @RequestParam(defaultValue = "0") int page,
                                                                                        @RequestParam(defaultValue = "10") int size) {

        return ResponseBuilder.pagedFromDTO(clienteService.listarClientesPorNome(cpf, page, size));
    }




    @DeleteMapping("/remover/{id}")
    @Operation(summary = "Remover Cliente", description = "Endpoint responsável por remover cliente (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente removido com sucesso",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<Void>> removerCliente(@PathVariable Long id) {

        clienteService.RemoverCliente(id);
        return ResponseBuilder.noContent();
    }

}