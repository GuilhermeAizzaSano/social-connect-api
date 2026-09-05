package br.com.socialconnect.api.beneficiarios.controller;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioDTO;
import br.com.socialconnect.api.beneficiarios.service.BeneficiarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiarios")
public class BeneficiarioController {

    private final BeneficiarioService service;

    public BeneficiarioController(BeneficiarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BeneficiarioDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{idBeneficiario}")
    public ResponseEntity<BeneficiarioDTO> buscarPorId(@PathVariable Long idBeneficiario) {
        return ResponseEntity.ok(service.buscarPorId(idBeneficiario));
    }

    @PostMapping
    public ResponseEntity<BeneficiarioDTO> salvar(@RequestBody BeneficiarioDTO dto) {
        BeneficiarioDTO salvo = service.salvar(dto);
        URI location = URI.create("/api/v1/beneficiarios/" + salvo.idBeneficiario());
        return ResponseEntity.created(location).body(salvo);
    }

    @DeleteMapping("/{idBeneficiario}")
    public ResponseEntity<Void> deletar(@PathVariable Long idBeneficiario) {
        service.deletar(idBeneficiario);
        return ResponseEntity.noContent().build();
    }
}