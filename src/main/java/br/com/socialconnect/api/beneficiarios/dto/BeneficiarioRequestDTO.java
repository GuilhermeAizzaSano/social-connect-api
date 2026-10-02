package br.com.socialconnect.api.beneficiarios.dto;

import br.com.socialconnect.api.validation.CPF;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BeneficiarioRequestDTO(

    @Schema(description = "Nome completo do beneficiário", example = "Maria da Silva")
    @NotBlank(message = "{NotBlank.nome}")
    @Size(max = 150, message = "{Size.nome}")
    String nome,

    @Schema(description = "CPF sem formatação", example = "12345678900")
    @NotBlank(message = "{NotBlank.cpf}")
    @CPF
    String cpf,

    @Schema(description = "Telefone com DDD", example = "11999999999")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    String telefone,

    @Schema(description = "Endereço completo", example = "Rua das Flores, 123")
    @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
    String endereco,

    @Schema(description = "Descrição da situação de vulnerabilidade", example = "Renda familiar baixa")
    @Size(max = 500, message = "Situação deve ter no máximo 500 caracteres")
    String situacaoVulnerabilidade

) {}
