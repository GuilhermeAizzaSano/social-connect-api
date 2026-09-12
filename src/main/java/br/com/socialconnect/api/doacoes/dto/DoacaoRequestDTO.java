package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DoacaoRequestDTO(
        @Schema(description = "ID do doador associado à doação", example = "10")
        @NotNull(message = "ID do doador é obrigatório")
        Long idDoador,

        @Schema(description = "Data em que a doação foi realizada", example = "2026-08-20")
        @NotNull(message = "Data da doação é obrigatória")
        LocalDate dataDoacao,

        @Schema(description = "Valor financeiro da doação", example = "250.50")
        BigDecimal valor,

        @Schema(description = "Tipo da doação", example = "FINANCEIRA")
        @NotNull(message = "Tipo de doação é obrigatório")
        TipoDoacao tipo,

        @Schema(description = "Descrição ou detalhes da doação", example = "Doação para campanha do agasalho")
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao
) {}
