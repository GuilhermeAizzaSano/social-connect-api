package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DoacaoPatchDTO(
        @Schema(description = "Novo ID do doador (opcional)", example = "10")
        Long idDoador,

        @Schema(description = "Nova data da doação (opcional)", example = "2026-08-22")
        LocalDate dataDoacao,

        @Schema(description = "Novo valor da doação (opcional)", example = "350.00")
        BigDecimal valor,

        @Schema(description = "Novo tipo da doação (opcional)", example = "ALIMENTO")
        TipoDoacao tipo,

        @Schema(description = "Nova descrição da doação (opcional)", example = "Cestas básicas atualizadas")
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao
) {}
