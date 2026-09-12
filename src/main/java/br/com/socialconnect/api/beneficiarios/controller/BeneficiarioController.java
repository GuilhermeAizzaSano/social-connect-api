package br.com.socialconnect.api.beneficiarios.controller;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
            description = "Retorna uma lista com todos os beneficiários cadastrados"
    )
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<BeneficiarioResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
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