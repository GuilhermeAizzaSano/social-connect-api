package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.exception.RecursoNaoEncontradoException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DoacaoService {

    private final DoacaoRepository repository;

    public DoacaoService(DoacaoRepository repository) {
        this.repository = repository;
    }

    public Page<DoacaoResponseDTO> listar(LocalDate dataInicio, LocalDate dataFim, TipoDoacao tipo, Pageable pageable) {
        Page<Doacao> page = repository.findComFiltros(dataInicio, dataFim, tipo, pageable);
        return page.map(this::toResponseDTO);
    }

    public DoacaoResponseDTO buscarPorId(Long idDoacao) {
        return repository.findById(idDoacao)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doação não encontrada: " + idDoacao));
    }

    public DoacaoResponseDTO criar(DoacaoRequestDTO dto) {
        Doacao entity = Doacao.builder()
                .idDoador(dto.idDoador())
                .dataDoacao(dto.dataDoacao())
                .valor(dto.valor())
                .tipo(dto.tipo())
                .descricao(dto.descricao())
                .build();
        return toResponseDTO(repository.save(entity));
    }

    public DoacaoResponseDTO atualizar(Long idDoacao, DoacaoRequestDTO dto) {
        Doacao entity = repository.findById(idDoacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doação não encontrada: " + idDoacao));

        entity.setIdDoador(dto.idDoador());
        entity.setDataDoacao(dto.dataDoacao());
        entity.setValor(dto.valor());
        entity.setTipo(dto.tipo());
        entity.setDescricao(dto.descricao());

        return toResponseDTO(repository.save(entity));
    }

    public DoacaoResponseDTO atualizarParcial(Long idDoacao, DoacaoPatchDTO dto) {
        Doacao entity = repository.findById(idDoacao)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Doação não encontrada: " + idDoacao));

        if (dto.idDoador() != null) entity.setIdDoador(dto.idDoador());
        if (dto.dataDoacao() != null) entity.setDataDoacao(dto.dataDoacao());
        if (dto.valor() != null) entity.setValor(dto.valor());
        if (dto.tipo() != null) entity.setTipo(dto.tipo());
        if (dto.descricao() != null) entity.setDescricao(dto.descricao());

        return toResponseDTO(repository.save(entity));
    }

    public void deletar(Long idDoacao) {
        if (!repository.existsById(idDoacao)) {
            throw new RecursoNaoEncontradoException("Doação não encontrada: " + idDoacao);
        }
        repository.deleteById(idDoacao);
    }

    private DoacaoResponseDTO toResponseDTO(Doacao entity) {
        return new DoacaoResponseDTO(
                entity.getIdDoacao(),
                entity.getIdDoador(),
                entity.getDataDoacao(),
                entity.getValor(),
                entity.getTipo(),
                entity.getDescricao()
        );
    }
}
