package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.service.DoacaoService;
import br.com.socialconnect.api.exception.ProblemDetail;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/doacoes")
@Tag(name = "Doações", description = "API para gestão de doações")
public class DoacaoController {

    private final DoacaoService service;

    public DoacaoController(DoacaoService service) {
        this.service = service;
    }

    // GET com paginação e filtros
    @GetMapping
    @Operation(
            summary = "Lista doações com paginação e filtros",
            description = "Retorna uma página de doações cadastradas, permitindo filtrar por intervalo de datas e tipo"
    )
    @ApiResponse(responseCode = "200", description = "Página de doações retornada com sucesso")
    public ResponseEntity<Page<DoacaoResponseDTO>> listar(
            @Parameter(description = "Data inicial para filtro (inclusive)", example = "2026-08-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,

            @Parameter(description = "Data final para filtro (inclusive)", example = "2026-08-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,

            @Parameter(description = "Tipo de doação para filtro", example = "FINANCEIRA")
            @RequestParam(required = false) TipoDoacao tipo,

            @Parameter(hidden = true)
            @PageableDefault(size = 20, sort = "dataDoacao") Pageable pageable) {
        return ResponseEntity.ok(service.listar(dataInicio, dataFim, tipo, pageable));
    }

    // GET por ID
    @GetMapping("/{idDoacao}")
    @Operation(
            summary = "Busca doação por ID",
            description = "Recupera os detalhes de uma doação a partir de seu ID identificador"
    )
    @ApiResponse(responseCode = "200", description = "Doação encontrada com sucesso")
    @ApiResponse(responseCode = "404", description = "Doação não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> buscarPorId(
            @Parameter(description = "Identificador único da doação", example = "1")
            @PathVariable Long idDoacao) {
        return ResponseEntity.ok(service.buscarPorId(idDoacao));
    }

    // POST com validação e 201 Created
    @PostMapping
    @Operation(
            summary = "Registra uma nova doação",
            description = "Cadastra uma doação no sistema com validação e retorna a localização do novo recurso"
    )
    @ApiResponse(responseCode = "201", description = "Doação registrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados da requisição inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> criar(
            @Valid @RequestBody DoacaoRequestDTO dto) {
        DoacaoResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/doacoes/" + salvo.idDoacao());
        return ResponseEntity.created(location).body(salvo);
    }

    // PUT (substituição total)
    @PutMapping("/{idDoacao}")
    @Operation(
            summary = "Atualiza uma doação completamente",
            description = "Substitui todos os dados da doação informada pelo ID"
    )
    @ApiResponse(responseCode = "200", description = "Doação atualizada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Doação não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> atualizar(
            @Parameter(description = "Identificador único da doação", example = "1")
            @PathVariable Long idDoacao,
            @Valid @RequestBody DoacaoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idDoacao, dto));
    }

    // PATCH (atualização parcial)
    @PatchMapping("/{idDoacao}")
    @Operation(
            summary = "Atualiza campos específicos de uma doação",
            description = "Modifica apenas os campos informados na requisição"
    )
    @ApiResponse(responseCode = "200", description = "Doação atualizada parcialmente com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados fornecidos inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Doação não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<DoacaoResponseDTO> atualizarParcial(
            @Parameter(description = "Identificador único da doação", example = "1")
            @PathVariable Long idDoacao,
            @Valid @RequestBody DoacaoPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idDoacao, dto));
    }

    // DELETE com 204 No Content
    @DeleteMapping("/{idDoacao}")
    @Operation(
            summary = "Remove uma doação",
            description = "Exclui o registro da doação correspondente ao ID informado"
    )
    @ApiResponse(responseCode = "204", description = "Doação removida com sucesso")
    @ApiResponse(responseCode = "404", description = "Doação não encontrada", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador único da doação", example = "1")
            @PathVariable Long idDoacao) {
        service.deletar(idDoacao);
        return ResponseEntity.noContent().build();
    }
}
