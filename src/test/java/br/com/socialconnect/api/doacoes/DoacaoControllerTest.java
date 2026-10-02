package br.com.socialconnect.api.doacoes;

import br.com.socialconnect.api.doacoes.dto.DoacaoPatchDTO;
import br.com.socialconnect.api.doacoes.dto.DoacaoRequestDTO;
import br.com.socialconnect.api.doacoes.model.TipoDoacao;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
@ActiveProfiles("test")
class DoacaoControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM doadores WHERE id_doador = 10", Integer.class);
        if (count == null || count == 0) {
            jdbcTemplate.update("INSERT INTO doadores (id_doador, nome, tipo, email, telefone) VALUES (10, 'Doador Teste', 'PESSOA_FISICA', 'doador@teste.com', '11999998888')");
        }
    }

    @Test
    @DisplayName("Deve executar o fluxo CRUD completo de Doações com semântica HTTP correta e salvar responses em JSON")
    void deveExecutarCrudCompletoDoacao() throws Exception {
        Map<String, Object> responsesLog = new LinkedHashMap<>();

        // 1. POST - Criar doação (201 Created com Location)
        DoacaoRequestDTO requestDTO = new DoacaoRequestDTO(
                10L,
                LocalDate.of(2026, 8, 20),
                new BigDecimal("250.50"),
                TipoDoacao.FINANCEIRA,
                "Doação para campanha do agasalho"
        );

        var postResult = mockMvc.perform(post("/api/v1/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/v1/doacoes/")))
                .andExpect(jsonPath("$.idDoacao").isNumber())
                .andExpect(jsonPath("$.idDoador").value(10))
                .andExpect(jsonPath("$.valor").value(250.50))
                .andExpect(jsonPath("$.tipo").value("FINANCEIRA"))
                .andExpect(jsonPath("$.descricao").value("Doação para campanha do agasalho"))
                .andReturn();

        String postResponse = postResult.getResponse().getContentAsString();
        responsesLog.put("1_POST_criar_status", postResult.getResponse().getStatus());
        responsesLog.put("1_POST_location_header", postResult.getResponse().getHeader("Location"));
        responsesLog.put("1_POST_response_body", objectMapper.readValue(postResponse, Object.class));

        Number idNumber = com.jayway.jsonpath.JsonPath.read(postResponse, "$.idDoacao");
        Long idDoacao = idNumber.longValue();

        // 2. GET por ID (200 OK)
        var getByIdResult = mockMvc.perform(get("/api/v1/doacoes/{idDoacao}", idDoacao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDoacao").value(idDoacao))
                .andExpect(jsonPath("$.idDoador").value(10))
                .andReturn();

        responsesLog.put("2_GET_por_id_status", getByIdResult.getResponse().getStatus());
        responsesLog.put("2_GET_por_id_response_body", objectMapper.readValue(getByIdResult.getResponse().getContentAsString(), Object.class));

        // 3. GET paginado com filtros (200 OK)
        var getListResult = mockMvc.perform(get("/api/v1/doacoes")
                        .param("tipo", "FINANCEIRA")
                        .param("dataInicio", "2026-08-01")
                        .param("dataFim", "2026-08-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)))
                .andReturn();

        responsesLog.put("3_GET_paginado_filtros_status", getListResult.getResponse().getStatus());
        responsesLog.put("3_GET_paginado_filtros_response_body", objectMapper.readValue(getListResult.getResponse().getContentAsString(), Object.class));

        // 4. PUT - Substituição total (200 OK)
        DoacaoRequestDTO putDTO = new DoacaoRequestDTO(
                10L,
                LocalDate.of(2026, 8, 21),
                new BigDecimal("300.00"),
                TipoDoacao.ALIMENTO,
                "Cestas básicas atualizadas"
        );

        var putResult = mockMvc.perform(put("/api/v1/doacoes/{idDoacao}", idDoacao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(putDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDoacao").value(idDoacao))
                .andExpect(jsonPath("$.valor").value(300.00))
                .andExpect(jsonPath("$.tipo").value("ALIMENTO"))
                .andExpect(jsonPath("$.descricao").value("Cestas básicas atualizadas"))
                .andReturn();

        responsesLog.put("4_PUT_substituicao_total_status", putResult.getResponse().getStatus());
        responsesLog.put("4_PUT_substituicao_total_response_body", objectMapper.readValue(putResult.getResponse().getContentAsString(), Object.class));

        // 5. PATCH - Atualização parcial (200 OK)
        DoacaoPatchDTO patchDTO = new DoacaoPatchDTO(
                null,
                null,
                new BigDecimal("350.00"),
                null,
                null
        );

        var patchResult = mockMvc.perform(patch("/api/v1/doacoes/{idDoacao}", idDoacao)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idDoacao").value(idDoacao))
                .andExpect(jsonPath("$.valor").value(350.00))
                .andExpect(jsonPath("$.tipo").value("ALIMENTO")) // Mantém inalterado
                .andExpect(jsonPath("$.descricao").value("Cestas básicas atualizadas")) // Mantém inalterado
                .andReturn();

        responsesLog.put("5_PATCH_atualizacao_parcial_status", patchResult.getResponse().getStatus());
        responsesLog.put("5_PATCH_atualizacao_parcial_response_body", objectMapper.readValue(patchResult.getResponse().getContentAsString(), Object.class));

        // 6. DELETE - Remoção (204 No Content)
        var deleteResult = mockMvc.perform(delete("/api/v1/doacoes/{idDoacao}", idDoacao))
                .andExpect(status().isNoContent())
                .andReturn();

        responsesLog.put("6_DELETE_status", deleteResult.getResponse().getStatus());

        // 7. GET após exclusão (Retorna 404 Not Found com Problem Details RFC 7807)
        var getAfterDeleteResult = mockMvc.perform(get("/api/v1/doacoes/{idDoacao}", idDoacao))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andReturn();

        responsesLog.put("7_GET_apos_delete_status", getAfterDeleteResult.getResponse().getStatus());
        responsesLog.put("7_GET_apos_delete_response_body", objectMapper.readValue(getAfterDeleteResult.getResponse().getContentAsString(), Object.class));

        // Salvar responses em arquivo JSON
        File outputFile = new File("target/doacoes-crud-responses.json");
        objectMapper.writeValue(outputFile, responsesLog);

        File rootOutputFile = new File("doacoes-crud-responses.json");
        objectMapper.writeValue(rootOutputFile, responsesLog);
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request com RFC 7807 ao tentar registrar doação com data futura")
    void deveRetornar400QuandoDataFutura() throws Exception {
        DoacaoRequestDTO requestInvalido = new DoacaoRequestDTO(
                10L,
                LocalDate.now().plusDays(10),
                new BigDecimal("100.00"),
                TipoDoacao.FINANCEIRA,
                "Doação com data futura"
        );

        mockMvc.perform(post("/api/v1/doacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://socialconnect.api/errors/validacao"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail", containsString("inválidos")))
                .andExpect(jsonPath("$.errors[0].message", containsString("futuro")));
    }
}
