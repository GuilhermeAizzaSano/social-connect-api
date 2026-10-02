package br.com.socialconnect.api.exception;

public class NomeProdutoDuplicadoException extends RuntimeException {
    private final String nome;

    public NomeProdutoDuplicadoException(String nome) {
        super("Já existe um produto cadastrado com o nome: " + nome);
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
