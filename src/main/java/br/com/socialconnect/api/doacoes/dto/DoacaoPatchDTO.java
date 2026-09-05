package br.com.socialconnect.api.doacoes.dto;

import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record DoacaoPatchDTO(
        Long idDoador,
        LocalDate dataDoacao,
        BigDecimal valor,
        TipoDoacao tipo,
        @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
        String descricao
) {}
