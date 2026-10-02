package br.com.socialconnect.api.doacoes.controller;

import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoResponseDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Requer Docker daemon ativo para execução com Testcontainers")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class DoacaoControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Deve criar doação quando dados válidos")
    void deveCriarDoacaoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        DoacaoRequestDTO requestDTO = new DoacaoRequestDTO(
                1L,
                LocalDate.of(2026, 9, 11),
                new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO,
                "Doação de teste integração"
        );

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ResponseEntity<DoacaoResponseDTO> response = restTemplate.postForEntity(
                "/api/v1/doacoes",
                requestDTO,
                DoacaoResponseDTO.class
        );

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().idDoacao(), "ID da doação não deve ser nulo");
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando data for no futuro")
    void deveRetornar400QuandoDataFutura() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        DoacaoRequestDTO requestDTO = new DoacaoRequestDTO(
                1L,
                LocalDate.now().plusDays(10),
                new BigDecimal("100.00"),
                TipoDoacao.ALIMENTO,
                "Doação com data futura"
        );

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/doacoes",
                requestDTO,
                String.class
        );

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("futuro"), "Mensagem de erro deve conter 'futuro'");
    }
}
