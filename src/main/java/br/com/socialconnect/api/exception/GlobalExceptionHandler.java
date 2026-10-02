package br.com.socialconnect.api.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Erro de validação (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {

        List<ProblemDetail.FieldError> errors = ex.getBindingResult()
                .getFieldErrors().stream()
                .map(e -> new ProblemDetail.FieldError(
                        e.getField(),
                        e.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        boolean hasEstoqueNegativo = ex.getBindingResult()
                .getFieldErrors().stream()
                .anyMatch(e -> "estoqueAtual".equals(e.getField()));

        if (hasEstoqueNegativo) {
            ProblemDetail problem = new ProblemDetail(
                    "https://socialconnect.api/errors/estoque-invalido",
                    "Estoque inválido",
                    HttpStatus.UNPROCESSABLE_ENTITY.value(),
                    "Estoque atual não pode ser negativo.",
                    request.getDescription(false),
                    LocalDateTime.now(),
                    errors
            );
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
        }

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/validacao",
                "Erro de validação",
                HttpStatus.BAD_REQUEST.value(),
                "Um ou mais campos são inválidos.",
                request.getDescription(false),
                LocalDateTime.now(),
                errors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    // Estoque inválido (422)
    @ExceptionHandler(EstoqueInvalidoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ProblemDetail> handleEstoqueInvalido(
            EstoqueInvalidoException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/estoque-invalido",
                "Estoque inválido",
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    // Nome de produto duplicado (409)
    @ExceptionHandler(NomeProdutoDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ProblemDetail> handleNomeProdutoDuplicado(
            NomeProdutoDuplicadoException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/nome-produto-duplicado",
                "Nome de produto já cadastrado",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    // CPF duplicado (409)
    @ExceptionHandler(CpfDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ProblemDetail> handleCpfDuplicado(
            CpfDuplicadoException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/cpf-duplicado",
                "CPF já cadastrado",
                HttpStatus.CONFLICT.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    // Recurso não encontrado (404)
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ProblemDetail> handleNaoEncontrado(
            RecursoNaoEncontradoException ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/nao-encontrado",
                "Recurso não encontrado",
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                request.getDescription(false),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // Erro genérico (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ProblemDetail> handleGenerico(
            Exception ex, WebRequest request) {

        ProblemDetail problem = new ProblemDetail(
                "https://socialconnect.api/errors/erro-interno",
                "Erro interno do servidor",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado. Tente novamente.",
                request.getDescription(false),
                LocalDateTime.now(),
                List.of()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
