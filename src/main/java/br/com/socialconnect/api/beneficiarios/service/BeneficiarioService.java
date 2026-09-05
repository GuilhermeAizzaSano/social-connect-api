package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository repository;

    // Injeção de dependência via construtor (Boa prática para testes)
    public BeneficiarioService(BeneficiarioRepository repository) {
        this.repository = repository;
    }

    public List<BeneficiarioDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public BeneficiarioDTO buscarPorId(Long idBeneficiario) {
        Beneficiario beneficiario = repository.findById(idBeneficiario)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado com o ID: " + idBeneficiario));
        return toDTO(beneficiario);
    }

    public BeneficiarioDTO salvar(BeneficiarioDTO dto) {
        Beneficiario beneficiario = toEntity(dto);
        beneficiario = repository.save(beneficiario);
        return toDTO(beneficiario);
    }

    public void deletar(Long idBeneficiario) {
        repository.deleteById(idBeneficiario);
    }

    private BeneficiarioDTO toDTO(Beneficiario entity) {
        return new BeneficiarioDTO(
                entity.getIdBeneficiario(), entity.getNome(), entity.getCpf(),
                entity.getTelefone(), entity.getEndereco(), entity.getSituacaoVulnerabilidade(),
                entity.getDataCadastro()
        );
    }

    private Beneficiario toEntity(BeneficiarioDTO dto) {
        return Beneficiario.builder()
                .idBeneficiario(dto.idBeneficiario())
                .nome(dto.nome()).cpf(dto.cpf()).telefone(dto.telefone())
                .endereco(dto.endereco()).situacaoVulnerabilidade(dto.situacaoVulnerabilidade())
                .dataCadastro(dto.dataCadastro())
                .build();
    }
}