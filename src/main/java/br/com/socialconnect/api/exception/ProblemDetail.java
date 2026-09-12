package br.com.socialconnect.api.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Estrutura padronizada de erro conforme RFC 7807 (Problem Details)")
public record ProblemDetail(
    @Schema(description = "URI de identificação do tipo do erro", example = "https://socialconnect.api/errors/validacao")
    String type,

    @Schema(description = "Título legível e resumido do erro", example = "Erro de validação")
    String title,

    @Schema(description = "Código de status HTTP do erro", example = "400")
    int status,

    @Schema(description = "Descrição detalhada da ocorrência", example = "Um ou mais campos são inválidos.")
    String detail,

    @Schema(description = "URI da ocorrência onde o erro aconteceu", example = "uri=/api/v1/beneficiarios")
    String instance,

    @Schema(description = "Data e hora em que o erro foi registrado", example = "2026-09-11T21:49:15")
    LocalDateTime timestamp,

    @Schema(description = "Lista detalhada de erros de validação por campo")
    List<FieldError> errors
) {
    @Schema(description = "Detalhe do erro de um campo específico")
    public record FieldError(
        @Schema(description = "Nome do campo que gerou o erro", example = "nome")
        String field,

        @Schema(description = "Mensagem explicativa da validação", example = "Nome é obrigatório")
        String message
    ) {}
}
