package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DoacaoResponseDTO(
        @Schema(description = "Identificador único da doação", example = "1")
        Long idDoacao,

        @Schema(description = "Identificador do doador", example = "10")
        Long idDoador,

        @Schema(description = "Data da doação", example = "2026-08-20")
        LocalDate dataDoacao,

        @Schema(description = "Valor financeiro da doação", example = "250.50")
        BigDecimal valor,

        @Schema(description = "Tipo da doação", example = "FINANCEIRA")
        TipoDoacao tipo,

        @Schema(description = "Descrição detalhada da doação", example = "Doação para campanha do agasalho")
        String descricao
) {}
