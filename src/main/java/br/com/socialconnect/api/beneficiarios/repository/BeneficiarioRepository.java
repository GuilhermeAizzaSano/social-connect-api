package br.com.socialconnect.api.beneficiarios.repository;

import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

    Optional<Beneficiario> findByCpf(String cpf);

    List<Beneficiario> findByNomeContainingIgnoreCase(String nome);

    boolean existsByCpf(String cpf);
}