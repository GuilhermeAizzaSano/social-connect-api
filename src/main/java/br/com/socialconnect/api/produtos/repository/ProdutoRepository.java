package br.com.socialconnect.api.produtos.repository;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.produtos.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdProdutoNot(String nome, Long idProduto);

    Optional<Produto> findByNomeIgnoreCase(String nome);

    @Query("SELECT p FROM Produto p WHERE " +
           "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:categoria IS NULL OR p.categoria = :categoria)")
    Page<Produto> findComFiltros(
            @Param("nome") String nome,
            @Param("categoria") CategoriaProduto categoria,
            Pageable pageable
    );
}
