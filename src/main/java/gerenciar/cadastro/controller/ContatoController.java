package gerenciar.cadastro.controller;

import gerenciar.cadastro.dto.request.ContatoAtualizacaoDTO;
import gerenciar.cadastro.dto.request.ContatoRequisicaoDto;
import gerenciar.cadastro.dto.response.ContatoRespostaDto;
import gerenciar.cadastro.service.ContatoService;
import gerenciar.cadastro.response.PagedResponse;
import gerenciar.cadastro.response.ResponseBuilder;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import gerenciar.cadastro.response.ResponseApi;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/contatos")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contatos", description = "Endpoints para gerenciamento de contatos")
public class ContatoController {


    @Autowired
    ContatoService contatoService;


    @PostMapping
    @Operation(summary = "Cadastrar contato", description = "Endpoint responsável por cadastrar novos contatos e associá-lo a um cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contato salvo com sucesso",
                    content = @Content(schema = @Schema(implementation = ContatoRespostaDto.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "409", description = "Conflito de dados",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<ContatoRespostaDto>> CadastarContato (@RequestBody @Valid ContatoRequisicaoDto dto){

        return ResponseBuilder.created(contatoService.CadastarContato(dto),"Contato cadastrado com sucesso");
    }


    @PutMapping("/{id}")
    @Operation(summary = "Atualizar contato", description = "Endpoint responsável por atualizar contatos")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contato atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ContatoRespostaDto.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<ContatoRespostaDto>> AtualizarContato(@PathVariable Long id, @RequestBody @Valid ContatoAtualizacaoDTO dto) {

        return ResponseBuilder.ok(contatoService.AtualizarContato(id,dto),"Contato atualizado com sucesso");
    }


    @GetMapping
    @Operation(summary = "Listar contatos", description = "Endpoint responsável por listar todos os contatos na base de dados com paginação")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listagem retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = ContatoRespostaDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<PagedResponse<ContatoRespostaDto>>> listarTodosContatosPagindao(
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {

        return ResponseBuilder.pagedFromDTO(contatoService.listarTodosContatosPaginado(page, size));
    }


    @GetMapping("/buscar")
    @Operation(summary = "buscar contatos por email",description = "Endpoint responsável por buscar contatos pelo email completo ou parcial com paginação ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contato encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = ContatoRespostaDto.class))),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })

    public ResponseEntity<ResponseApi<PagedResponse<ContatoRespostaDto>>> buscarPorEmail(@RequestParam  String email,
                                                                                         @RequestParam(defaultValue = "0") int page,
                                                                                         @RequestParam(defaultValue = "10") int size) {

        return ResponseBuilder.pagedFromDTO(contatoService.listarContatosPorEmail(email, page, size));
    }





    @GetMapping("/{id}")
    @Operation(summary = "Listar contatos por cliente", description = "Endpoint responsável por listar contatos de um cliente específico por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contatos do cliente retornados com sucesso",
                    content = @Content(schema = @Schema(implementation = ContatoRespostaDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<List<ContatoRespostaDto>>> listarClientesByID(@PathVariable Long id){

        return ResponseBuilder.ok(contatoService.listarContatosByclienteId(id),"Contatos do cliente retornados com sucesso");
    }


    @DeleteMapping("/remover/{id}")
    @Operation(summary = "Remover contato", description = "Endpoint responsável por remover contatos (soft delete)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contato removido com sucesso",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "401", description = "Credenciais de autenticação inválidas",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "403", description = "Requisição não autorizada",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "Contato não encontrado",
                    content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "500", description = "Erro interno",
                    content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ResponseApi<Void>> removerContato(@PathVariable Long id) {

        contatoService.RemoverContato(id);
        return ResponseBuilder.noContent();
    }



}
