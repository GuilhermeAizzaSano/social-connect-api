package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueInvalidoException;
import br.com.socialconnect.api.exception.NomeProdutoDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ProdutoServiceImpl implements ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoServiceImpl(ProdutoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<ProdutoResponseDTO> listar(String nome, CategoriaProduto categoria, Pageable pageable) {
        return repository.findComFiltros(nome, categoria, pageable)
                .map(ProdutoResponseDTO::fromEntity);
    }

    @Override
    public ProdutoResponseDTO buscarPorId(Long id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com o ID: " + id));
        return ProdutoResponseDTO.fromEntity(produto);
    }

    @Override
    public ProdutoResponseDTO criar(ProdutoRequestDTO dto) {
        validarEstoqueNaoNegativo(dto);

        if (repository.existsByNomeIgnoreCase(dto.nome())) {
            throw new NomeProdutoDuplicadoException(dto.nome());
        }

        Produto produto = Produto.builder()
                .nome(dto.nome().trim())
                .categoria(dto.categoria())
                .estoqueAtual(dto.estoqueAtual())
                .estoqueMinimo(dto.estoqueMinimo())
                .unidadeMedida(dto.unidadeMedida().trim())
                .dataCadastro(LocalDate.now())
                .build();

        return ProdutoResponseDTO.fromEntity(repository.save(produto));
    }

    @Override
    public ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com o ID: " + id));

        validarEstoqueNaoNegativo(dto);

        if (repository.existsByNomeIgnoreCaseAndIdProdutoNot(dto.nome(), id)) {
            throw new NomeProdutoDuplicadoException(dto.nome());
        }

        produto.setNome(dto.nome().trim());
        produto.setCategoria(dto.categoria());
        produto.setEstoqueAtual(dto.estoqueAtual());
        produto.setEstoqueMinimo(dto.estoqueMinimo());
        produto.setUnidadeMedida(dto.unidadeMedida().trim());

        return ProdutoResponseDTO.fromEntity(repository.save(produto));
    }

    @Override
    public void deletar(Long id) {
        Produto produto = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado com o ID: " + id));
        repository.delete(produto);
    }

    private void validarEstoqueNaoNegativo(ProdutoRequestDTO dto) {
        if (dto.estoqueAtual() != null && dto.estoqueAtual() < 0) {
            throw new EstoqueInvalidoException("Estoque atual não pode ser negativo: " + dto.estoqueAtual());
        }
        if (dto.estoqueMinimo() != null && dto.estoqueMinimo() < 0) {
            throw new EstoqueInvalidoException("Estoque mínimo não pode ser negativo: " + dto.estoqueMinimo());
        }
    }
}
