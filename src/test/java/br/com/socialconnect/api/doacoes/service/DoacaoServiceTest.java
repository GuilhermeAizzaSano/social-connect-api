package br.com.socialconnect.api.doacoes.service;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.Doacao;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import br.com.socialconnect.api.doacoes.repository.DoacaoRepository;
import br.com.socialconnect.api.doadores.model.Doador;
import br.com.socialconnect.api.doadores.repository.DoadorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class DoacaoServiceTest {

    @Mock
    private DoacaoRepository doacaoRepository;

    @Mock
    private DoadorRepository doadorRepository;

    @InjectMocks
    private DoacaoService doacaoService;

    @Test
    @DisplayName("Deve criar doação quando dados válidos")
    void deveCriarDoacaoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                1L, LocalDate.of(2026, 9, 11),
                new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO,
                "Doação de teste"
        );
        Doador doador = Doador.builder().idDoador(1L).nome("Doador 1").tipo("PESSOA_FISICA").build();
        Doacao doacaoSalva = Doacao.builder()
                .idDoacao(1L)
                .idDoador(1L)
                .dataDoacao(LocalDate.of(2026, 9, 11))
                .valor(new BigDecimal("100.00"))
                .tipo(TipoDoacao.ALIMENTO)
                .descricao("Doação de teste")
                .build();

        Mockito.when(doadorRepository.findById(1L)).thenReturn(Optional.of(doador));
        Mockito.when(doacaoRepository.save(Mockito.any())).thenReturn(doacaoSalva);

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        DoacaoResponseDTO resultado = doacaoService.criar(dto);

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        Assertions.assertNotNull(resultado.idDoacao(), "ID da doação não deve ser nulo");
        Assertions.assertEquals(new BigDecimal("100.00"), resultado.valor());
        Mockito.verify(doadorRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(doacaoRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando doador não existir")
    void deveLancarExcecaoQuandoDoadorNaoExistir() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        DoacaoRequestDTO dto = new DoacaoRequestDTO(
                999L, LocalDate.of(2026, 9, 11),
                new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO,
                "Doação de teste"
        );
        Mockito.when(doadorRepository.findById(999L)).thenReturn(Optional.empty());

        // ==========================================
        // ACT & ASSERT: Executar a ação e verificar exceção
        // ==========================================
        Assertions.assertThrows(RuntimeException.class, () -> {
            doacaoService.criar(dto);
        });

        Mockito.verify(doadorRepository, Mockito.times(1)).findById(999L);
        Mockito.verify(doacaoRepository, Mockito.never()).save(Mockito.any());
    }
}
