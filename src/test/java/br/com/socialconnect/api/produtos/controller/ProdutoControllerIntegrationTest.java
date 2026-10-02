package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.exception.ProblemDetail;
import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.dto.ProdutoResponseDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class ProdutoControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Deve criar produto quando dados válidos (espera 201)")
    void deveCriarProdutoQuandoDadosValidos() {
        // ==========================================
        // ARRANGE: Preparar o cenário
        // ==========================================
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO(
                "Leite Integral 1L",
                CategoriaProduto.ALIMENTO,
                50,
                20,
                "unidade"
        );

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ResponseEntity<ProdutoResponseDTO> response = restTemplate.postForEntity(
                "/api/v1/produtos",
                requestDTO,
                ProdutoResponseDTO.class
        );

        // ==========================================
        // ASSERT: Verificar o resultado
        // ==========================================
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().idProduto());
        assertEquals("Leite Integral 1L", response.getBody().nome());
        assertNotNull(response.getHeaders().getLocation());
    }

    @Test
    @DisplayName("Deve retornar 409 quando nome duplicado")
    void deveRetornar409QuandoNomeDuplicado() {
        // ==========================================
        // ARRANGE: Preparar o cenário e cadastrar primeiro produto
        // ==========================================
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO(
                "Macarrão 500g",
                CategoriaProduto.ALIMENTO,
                30,
                10,
                "unidade"
        );

        restTemplate.postForEntity("/api/v1/produtos", requestDTO, ProdutoResponseDTO.class);

        // ==========================================
        // ACT: Tentar cadastrar produto com mesmo nome
        // ==========================================
        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity(
                "/api/v1/produtos",
                requestDTO,
                ProblemDetail.class
        );

        // ==========================================
        // ASSERT: Verificar retorno 409 Conflict
        // ==========================================
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().status());
    }

    @Test
    @DisplayName("Deve retornar 422 quando estoque negativo")
    void deveRetornar422QuandoEstoqueNegativo() {
        // ==========================================
        // ARRANGE: Preparar o cenário com estoque negativo
        // ==========================================
        ProdutoRequestDTO requestDTO = new ProdutoRequestDTO(
                "Detergente 500ml",
                CategoriaProduto.HIGIENE,
                -5,
                10,
                "frasco"
        );

        // ==========================================
        // ACT: Executar a ação
        // ==========================================
        ResponseEntity<ProblemDetail> response = restTemplate.postForEntity(
                "/api/v1/produtos",
                requestDTO,
                ProblemDetail.class
        );

        // ==========================================
        // ASSERT: Verificar retorno 422 Unprocessable Entity
        // ==========================================
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().status());
    }
}
