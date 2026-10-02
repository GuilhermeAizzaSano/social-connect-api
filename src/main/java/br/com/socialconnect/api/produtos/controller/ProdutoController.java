package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.exception.ProblemDetail;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/produtos")
@Tag(name = "Produtos", description = "API para gestão de produtos e controle de estoque")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Lista todos os produtos",
            description = "Retorna uma lista paginada de produtos com filtros opcionais por nome e categoria"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Page<ProdutoResponseDTO>> listar(
            @Parameter(description = "Nome para filtrar (parcial)", example = "Arroz")
            @RequestParam(required = false) String nome,
            @Parameter(description = "Categoria do produto para filtrar", example = "ALIMENTO")
            @RequestParam(required = false) CategoriaProduto categoria,
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, categoria, pageable));
    }

    @GetMapping("/{id_produto}")
    @Operation(
            summary = "Busca um produto pelo ID",
            description = "Retorna os dados detalhados do produto correspondente ao ID informado"
    )
    @ApiResponse(responseCode = "200", description = "Produto encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(
            @Parameter(description = "Identificador único do produto", example = "1")
            @PathVariable("id_produto") Long idProduto) {
        return ResponseEntity.ok(service.buscarPorId(idProduto));
    }

    @PostMapping
    @Operation(
            summary = "Cadastra um novo produto",
            description = "Cria um novo produto com controle de estoque e unicidade de nome"
    )
    @ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Nome de produto já cadastrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Estoque inválido ou negativo", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> criar(
            @Valid @RequestBody ProdutoRequestDTO dto) {
        ProdutoResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/produtos/" + salvo.idProduto());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{id_produto}")
    @Operation(
            summary = "Atualiza um produto por completo",
            description = "Substituição total dos dados do produto indicado pelo ID"
    )
    @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "Nome de produto já cadastrado em outro registro", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "422", description = "Estoque inválido ou negativo", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @Parameter(description = "Identificador único do produto", example = "1")
            @PathVariable("id_produto") Long idProduto,
            @Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idProduto, dto));
    }

    @DeleteMapping("/{id_produto}")
    @Operation(
            summary = "Remove um produto",
            description = "Exclui o registro do produto indicado pelo ID"
    )
    @ApiResponse(responseCode = "204", description = "Produto removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Produto não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador único do produto", example = "1")
            @PathVariable("id_produto") Long idProduto) {
        service.deletar(idProduto);
        return ResponseEntity.noContent().build();
    }
}
