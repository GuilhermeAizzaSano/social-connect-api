package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.service.DoacaoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/doacoes")
public class DoacaoController {

    private final DoacaoService service;

    public DoacaoController(DoacaoService service) {
        this.service = service;
    }

    // GET com paginação e filtros
    @GetMapping
    public ResponseEntity<Page<DoacaoResponseDTO>> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) TipoDoacao tipo,
            @PageableDefault(size = 20, sort = "dataDoacao") Pageable pageable) {
        return ResponseEntity.ok(service.listar(dataInicio, dataFim, tipo, pageable));
    }

    // GET por ID
    @GetMapping("/{idDoacao}")
    public ResponseEntity<DoacaoResponseDTO> buscarPorId(@PathVariable Long idDoacao) {
        return ResponseEntity.ok(service.buscarPorId(idDoacao));
    }

    // POST com validação e 201 Created
    @PostMapping
    public ResponseEntity<DoacaoResponseDTO> criar(@Valid @RequestBody DoacaoRequestDTO dto) {
        DoacaoResponseDTO salvo = service.criar(dto);
        URI location = URI.create("/api/v1/doacoes/" + salvo.idDoacao());
        return ResponseEntity.created(location).body(salvo);
    }

    // PUT (substituição total)
    @PutMapping("/{idDoacao}")
    public ResponseEntity<DoacaoResponseDTO> atualizar(
            @PathVariable Long idDoacao,
            @Valid @RequestBody DoacaoRequestDTO dto) {
        return ResponseEntity.ok(service.atualizar(idDoacao, dto));
    }

    // PATCH (atualização parcial)
    @PatchMapping("/{idDoacao}")
    public ResponseEntity<DoacaoResponseDTO> atualizarParcial(
            @PathVariable Long idDoacao,
            @Valid @RequestBody DoacaoPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(idDoacao, dto));
    }

    // DELETE com 204 No Content
    @DeleteMapping("/{idDoacao}")
    public ResponseEntity<Void> deletar(@PathVariable Long idDoacao) {
        service.deletar(idDoacao);
        return ResponseEntity.noContent().build();
    }
}
