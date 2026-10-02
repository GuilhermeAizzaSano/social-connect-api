package br.com.socialconnect.api.produtos.service;

import br.com.socialconnect.api.exception.EstoqueInvalidoException;
import br.com.socialconnect.api.exception.NomeProdutoDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import br.com.socialconnect.api.produtos.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository repository;

    @InjectMocks
    private ProdutoServiceImpl service;

    @Test
    @DisplayName("Deve criar produto quando dados válidos")
    void deveCriarProdutoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Arroz 5kg",
                CategoriaProduto.ALIMENTO,
                15,
                10,
                "unidade"
        );

        Produto produtoSalvo = Produto.builder()
                .idProduto(1L)
                .nome("Arroz 5kg")
                .categoria(CategoriaProduto.ALIMENTO)
                .estoqueAtual(15)
                .estoqueMinimo(10)
                .unidadeMedida("unidade")
                .dataCadastro(LocalDate.now())
                .build();

        Mockito.when(repository.existsByNomeIgnoreCase("Arroz 5kg")).thenReturn(false);
        Mockito.when(repository.save(Mockito.any(Produto.class))).thenReturn(produtoSalvo);

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ProdutoResponseDTO response = service.criar(dto);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        assertNotNull(response);
        assertEquals(1L, response.idProduto());
        assertEquals("Arroz 5kg", response.nome());
        assertEquals(CategoriaProduto.ALIMENTO, response.categoria());
        assertEquals(15, response.estoqueAtual());
        assertEquals(10, response.estoqueMinimo());
        assertEquals("unidade", response.unidadeMedida());
        assertFalse(response.estoqueBaixo());
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando estoque negativo")
    void deveLancarExcecaoQuandoEstoqueNegativo() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Feijão Preto",
                CategoriaProduto.ALIMENTO,
                -5,
                10,
                "kg"
        );

        // ==========================================
        // ACT & ASSERT: Executar e verificar exceção
        // ==========================================
        assertThrows(EstoqueInvalidoException.class, () -> service.criar(dto));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome duplicado")
    void deveLancarExcecaoQuandoNomeDuplicado() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Sabonete",
                CategoriaProduto.HIGIENE,
                20,
                5,
                "unidade"
        );

        Mockito.when(repository.existsByNomeIgnoreCase("Sabonete")).thenReturn(true);

        // ==========================================
        // ACT & ASSERT: Executar e verificar exceção
        // ==========================================
        assertThrows(NomeProdutoDuplicadoException.class, () -> service.criar(dto));
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(Produto.class));
    }

    @Test
    @DisplayName("Deve retornar estoqueBaixo true quando estoqueAtual menor que estoqueMinimo")
    void deveRetornarEstoqueBaixoTrueQuandoEstoqueAtualMenorQueMinimo() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        Produto produto = Produto.builder()
                .idProduto(2L)
                .nome("Óleo de Soja")
                .categoria(CategoriaProduto.ALIMENTO)
                .estoqueAtual(3)
                .estoqueMinimo(10)
                .unidadeMedida("litro")
                .dataCadastro(LocalDate.now())
                .build();

        Mockito.when(repository.findById(2L)).thenReturn(Optional.of(produto));

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ProdutoResponseDTO response = service.buscarPorId(2L);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        assertNotNull(response);
        assertTrue(response.estoqueBaixo());
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado por ID")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        Mockito.when(repository.findById(999L)).thenReturn(Optional.empty());

        // ==========================================
        // ACT & ASSERT: Executar e verificar exceção
        // ==========================================
        assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarPorId(999L));
    }

    @Test
    @DisplayName("Deve listar produtos com paginação e filtros")
    void deveListarProdutosComPaginacaoEFiltros() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        Pageable pageable = PageRequest.of(0, 10);
        Produto produto = Produto.builder()
                .idProduto(1L)
                .nome("Arroz 5kg")
                .categoria(CategoriaProduto.ALIMENTO)
                .estoqueAtual(15)
                .estoqueMinimo(10)
                .unidadeMedida("unidade")
                .dataCadastro(LocalDate.now())
                .build();
        Page<Produto> page = new PageImpl<>(List.of(produto));

        Mockito.when(repository.findComFiltros("Arroz", CategoriaProduto.ALIMENTO, pageable)).thenReturn(page);

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        Page<ProdutoResponseDTO> resultado = service.listar("Arroz", CategoriaProduto.ALIMENTO, pageable);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        assertEquals("Arroz 5kg", resultado.getContent().get(0).nome());
    }

    @Test
    @DisplayName("Deve deletar produto existente com sucesso")
    void deveDeletarProdutoExistente() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        Produto produto = Produto.builder()
                .idProduto(1L)
                .nome("Camisa M")
                .categoria(CategoriaProduto.ROUPA)
                .estoqueAtual(5)
                .estoqueMinimo(2)
                .unidadeMedida("unidade")
                .dataCadastro(LocalDate.now())
                .build();

        Mockito.when(repository.findById(1L)).thenReturn(Optional.of(produto));

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        service.deletar(1L);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Mockito.verify(repository, Mockito.times(1)).delete(produto);
    }
}
