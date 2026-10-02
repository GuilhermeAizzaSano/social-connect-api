package br.com.socialconnect.api.doacoes.repository;

import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DoacaoRepository extends JpaRepository<Doacao, Long> {

    // Método derivado pelo nome conforme Mob Programming
    Page<Doacao> findByDataDoacaoBetweenAndTipo(
            LocalDate dataInicio,
            LocalDate dataFim,
            TipoDoacao tipo,
            Pageable pageable
    );

    @Query("SELECT d FROM Doacao d WHERE " +
           "(:dataInicio IS NULL OR d.dataDoacao >= :dataInicio) AND " +
           "(:dataFim IS NULL OR d.dataDoacao <= :dataFim) AND " +
           "(:tipo IS NULL OR d.tipo = :tipo)")
    Page<Doacao> findComFiltros(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipo") TipoDoacao tipo,
            Pageable pageable
    );
}
