package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.service.BeneficiarioService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/beneficiarios")
@Tag(name = "Beneficiários", description = "API para gestão de beneficiários")
public class BeneficiarioController {

    private final BeneficiarioService service;

    public BeneficiarioController(BeneficiarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(
            summary = "Lista todos os beneficiários",
            description = "Retorna uma lista paginada de beneficiários com filtros opcionais"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    @ApiResponse(responseCode = "400", description = "Parâmetros inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Page<BeneficiarioResponseDTO>> listar(
            @Parameter(description = "Nome para filtrar (parcial)", example = "Maria")
            @RequestParam(required = false) String nome,
            @Parameter(description = "CPF para filtrar (exato)", example = "12345678900")
            @RequestParam(required = false) String cpf,
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listar(nome, cpf, pageable));
    }

    @GetMapping("/{idBeneficiario}")
    @Operation(
            summary = "Busca um beneficiário pelo ID",
            description = "Retorna os dados cadastrais do beneficiário correspondente ao ID informado"
    )
    @ApiResponse(responseCode = "200", description = "Beneficiário encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> buscarPorId(
            @Parameter(description = "Identificador único do beneficiário", example = "1")
            @PathVariable Long idBeneficiario) {
        return ResponseEntity.ok(service.buscarPorId(idBeneficiario));
    }

    @PostMapping
    @Operation(
            summary = "Cria um novo beneficiário",
            description = "Cadastra um novo beneficiário no sistema com validação prévia dos dados"
    )
    @ApiResponse(responseCode = "201", description = "Beneficiário criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> criar(
            @Valid @RequestBody BeneficiarioRequestDTO dto) {
        BeneficiarioResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/beneficiarios/" + salvo.idBeneficiario());
        return ResponseEntity.created(location).body(salvo);
    }

    @PutMapping("/{idBeneficiario}")
    @Operation(
            summary = "Atualiza um beneficiário por completo",
            description = "Substituição total dos dados do beneficiário indicado pelo ID"
    )
    @ApiResponse(responseCode = "200", description = "Beneficiário atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> atualizar(
            @Parameter(description = "Identificador único do beneficiário", example = "1")
            @PathVariable Long idBeneficiario,
            @Valid @RequestBody BeneficiarioRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idBeneficiario, dto));
    }

    @PatchMapping("/{idBeneficiario}")
    @Operation(
            summary = "Atualiza parcialmente um beneficiário",
            description = "Atualiza apenas os campos enviados no corpo da requisição"
    )
    @ApiResponse(responseCode = "200", description = "Beneficiário atualizado parcialmente com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<BeneficiarioResponseDTO> atualizarParcial(
            @Parameter(description = "Identificador único do beneficiário", example = "1")
            @PathVariable Long idBeneficiario,
            @Valid @RequestBody BeneficiarioPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idBeneficiario, dto));
    }

    @DeleteMapping("/{idBeneficiario}")
    @Operation(
            summary = "Remove um beneficiário",
            description = "Exclui o registro do beneficiário indicado pelo ID"
    )
    @ApiResponse(responseCode = "204", description = "Beneficiário removido com sucesso")
    @ApiResponse(responseCode = "404", description = "Beneficiário não encontrado", content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deletar(
            @Parameter(description = "Identificador único do beneficiário", example = "1")
            @PathVariable Long idBeneficiario) {
        service.deletar(idBeneficiario);
        return ResponseEntity.noContent().build();
    }
}