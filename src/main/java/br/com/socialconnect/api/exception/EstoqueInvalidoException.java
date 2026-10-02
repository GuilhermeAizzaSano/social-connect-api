package br.com.socialconnect.api.exception;

public class EstoqueInvalidoException extends RuntimeException {
    public EstoqueInvalidoException(String message) {
        super(message);
    }
}
