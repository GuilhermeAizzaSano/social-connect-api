package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import br.com.socialconnect.api.exception.CpfDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository repository;

    // Injeção de dependência via construtor (Boa prática para testes)
    public BeneficiarioService(BeneficiarioRepository repository) {
        this.repository = repository;
    }

    public List<BeneficiarioResponseDTO> listarTodos() {
        return repository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public BeneficiarioResponseDTO buscarPorId(Long idBeneficiario) {
        Beneficiario beneficiario = repository.findById(idBeneficiario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Beneficiário não encontrado com o ID: " + idBeneficiario));
        return toResponseDTO(beneficiario);
    }

    public BeneficiarioResponseDTO criar(BeneficiarioRequestDTO dto) {
        if (repository.existsByCpf(dto.cpf())) {
            throw new CpfDuplicadoException(dto.cpf());
        }
        Beneficiario beneficiario = toEntity(dto);
        beneficiario = repository.save(beneficiario);
        return toResponseDTO(beneficiario);
    }

    public void deletar(Long idBeneficiario) {
        if (!repository.existsById(idBeneficiario)) {
            throw new RecursoNaoEncontradoException("Beneficiário não encontrado com o ID: " + idBeneficiario);
        }
        repository.deleteById(idBeneficiario);
    }

    // Sobrecarga para compatibilidade com código existente
    public BeneficiarioDTO salvar(BeneficiarioDTO dto) {
        if (repository.existsByCpf(dto.cpf())) {
            throw new CpfDuplicadoException(dto.cpf());
        }
        Beneficiario beneficiario = Beneficiario.builder()
                .idBeneficiario(dto.idBeneficiario())
                .nome(dto.nome())
                .cpf(dto.cpf())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .situacaoVulnerabilidade(dto.situacaoVulnerabilidade())
                .dataCadastro(dto.dataCadastro() != null ? dto.dataCadastro() : LocalDate.now())
                .build();
        beneficiario = repository.save(beneficiario);
        return new BeneficiarioDTO(
                beneficiario.getIdBeneficiario(),
                beneficiario.getNome(),
                beneficiario.getCpf(),
                beneficiario.getTelefone(),
                beneficiario.getEndereco(),
                beneficiario.getSituacaoVulnerabilidade(),
                beneficiario.getDataCadastro()
        );
    }

    private BeneficiarioResponseDTO toResponseDTO(Beneficiario entity) {
        return new BeneficiarioResponseDTO(
                entity.getIdBeneficiario(),
                entity.getNome(),
                entity.getCpf(),
                entity.getTelefone(),
                entity.getEndereco(),
                entity.getSituacaoVulnerabilidade(),
                entity.getDataCadastro()
        );
    }

    private Beneficiario toEntity(BeneficiarioRequestDTO dto) {
        return Beneficiario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .telefone(dto.telefone())
                .endereco(dto.endereco())
                .situacaoVulnerabilidade(dto.situacaoVulnerabilidade())
                .dataCadastro(LocalDate.now())
                .build();
    }
}