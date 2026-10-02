package br.com.socialconnect.api.beneficiarios.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

public record BeneficiarioPatchDTO(
        @Schema(description = "Nome completo do beneficiário", example = "Maria da Silva Santos")
        @Size(max = 150, message = "{Size.nome}")
        String nome,

        @Schema(description = "Telefone com DDD", example = "11988887777")
        @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
        String telefone,

        @Schema(description = "Endereço completo", example = "Rua Nova, 456")
        @Size(max = 255, message = "Endereço deve ter no máximo 255 caracteres")
        String endereco,

        @Schema(description = "Descrição da situação de vulnerabilidade", example = "Renda familiar baixa")
        @Size(max = 500, message = "Situação deve ter no máximo 500 caracteres")
        String situacaoVulnerabilidade
) {}
