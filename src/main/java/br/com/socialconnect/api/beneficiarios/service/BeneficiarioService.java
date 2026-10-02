package br.com.socialconnect.api.beneficiarios.service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioResponseDTO;
import br.com.socialconnect.api.beneficiarios.model.Beneficiario;
import br.com.socialconnect.api.beneficiarios.repository.BeneficiarioRepository;
import br.com.socialconnect.api.exception.CpfDuplicadoException;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.stereotype.Service;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@Service
public class BeneficiarioService {

    private final BeneficiarioRepository repository;

    // Injeção de dependência via construtor (Boa prática para testes)
    public BeneficiarioService(BeneficiarioRepository repository) {
        this.repository = repository;
    }

    public Page<BeneficiarioResponseDTO> listar(String nome, String cpf, Pageable pageable) {
        Page<Beneficiario> page;
        if (cpf != null && !cpf.isBlank()) {
            page = repository.findByCpf(cpf, pageable);
        } else if (nome != null && !nome.isBlank()) {
            page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(this::toResponseDTO);
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

    public BeneficiarioResponseDTO atualizar(Long idBeneficiario, BeneficiarioRequestDTO dto) {
        Beneficiario entity = repository.findById(idBeneficiario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Beneficiário não encontrado com o ID: " + idBeneficiario));

        if (!entity.getCpf().equals(dto.cpf()) && repository.existsByCpf(dto.cpf())) {
            throw new CpfDuplicadoException(dto.cpf());
        }

        entity.setNome(dto.nome());
        entity.setCpf(dto.cpf());
        entity.setTelefone(dto.telefone());
        entity.setEndereco(dto.endereco());
        entity.setSituacaoVulnerabilidade(dto.situacaoVulnerabilidade());

        return toResponseDTO(repository.save(entity));
    }

    public BeneficiarioResponseDTO atualizarParcial(Long idBeneficiario, BeneficiarioPatchDTO dto) {
        Beneficiario entity = repository.findById(idBeneficiario)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Beneficiário não encontrado com o ID: " + idBeneficiario));

        if (dto.nome() != null) entity.setNome(dto.nome());
        if (dto.telefone() != null) entity.setTelefone(dto.telefone());
        if (dto.endereco() != null) entity.setEndereco(dto.endereco());
        if (dto.situacaoVulnerabilidade() != null) entity.setSituacaoVulnerabilidade(dto.situacaoVulnerabilidade());

        return toResponseDTO(repository.save(entity));
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