package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DoacaoRequestDTO(
        @NotNull(message = "ID do doador é obrigatório")
        Long idDoador,

        @NotNull(message = "Data da doação é obrigatória")
        LocalDate dataDoacao,

        BigDecimal valor,

        @NotNull(message = "Tipo de doação é obrigatório")
        TipoDoacao tipo,

        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao
) {}
