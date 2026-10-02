package br.com.socialconnect.api.doadores.repository;

import br.com.socialconnect.api.doadores.model.Doador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoadorRepository extends JpaRepository<Doador, Long> {
}
