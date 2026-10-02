package br.com.socialconnect.api.beneficiarios;

import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioPatchDTO;
import br.com.socialconnect.api.beneficiarios.dto.BeneficiarioRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class BeneficiarioControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Deve criar beneficiário com sucesso retornando 201 Created e Location")
    void deveCriarBeneficiarioComSucesso() throws Exception {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Maria da Silva",
                "12345678901",
                "11999999999",
                "Rua das Flores, 123",
                "Renda familiar baixa"
        );

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/v1/beneficiarios/")))
                .andExpect(jsonPath("$.idBeneficiario").isNumber())
                .andExpect(jsonPath("$.nome").value("Maria da Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.telefone").value("11999999999"))
                .andExpect(jsonPath("$.endereco").value("Rua das Flores, 123"))
                .andExpect(jsonPath("$.situacaoVulnerabilidade").value("Renda familiar baixa"))
                .andExpect(jsonPath("$.dataCadastro").isNotEmpty());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request com RFC 7807 ao enviar dados inválidos")
    void deveRetornar400QuandoDadosInvalidos() throws Exception {
        BeneficiarioRequestDTO dtoInvalido = new BeneficiarioRequestDTO(
                "", // Nome em branco
                "123", // CPF com tamanho inválido
                "11999999999",
                "Rua das Flores, 123",
                "Renda familiar baixa"
        );

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("https://socialconnect.api/errors/validacao"))
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("Um ou mais campos são inválidos."))
                .andExpect(jsonPath("$.errors", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict com RFC 7807 ao tentar cadastrar CPF duplicado")
    void deveRetornar409QuandoCpfDuplicado() throws Exception {
        BeneficiarioRequestDTO dto1 = new BeneficiarioRequestDTO(
                "João Pereira",
                "98765432100",
                "11988887777",
                "Av Paulista, 1000",
                "Desempregado"
        );

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        // Segunda tentativa com o mesmo CPF
        BeneficiarioRequestDTO dtoDuplicado = new BeneficiarioRequestDTO(
                "João Pereira Filho",
                "98765432100",
                "11911112222",
                "Av Paulista, 1000",
                "Outro motivo"
        );

        mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoDuplicado)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("https://socialconnect.api/errors/cpf-duplicado"))
                .andExpect(jsonPath("$.title").value("CPF já cadastrado"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail", containsString("98765432100")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found com RFC 7807 ao buscar ID inexistente")
    void deveRetornar404QuandoBeneficiarioNaoEncontrado() throws Exception {
        mockMvc.perform(get("/api/v1/beneficiarios/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("https://socialconnect.api/errors/nao-encontrado"))
                .andExpect(jsonPath("$.title").value("Recurso não encontrado"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail", containsString("99999")))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @DisplayName("Deve listar beneficiários retornando 200 OK com envelope paginado")
    void deveListarBeneficiarios() throws Exception {
        mockMvc.perform(get("/api/v1/beneficiarios"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("Deve atualizar beneficiário completamente com PUT retornando 200 OK")
    void deveAtualizarBeneficiarioComPut() throws Exception {
        BeneficiarioRequestDTO dtoCriacao = new BeneficiarioRequestDTO(
                "Lucas Alencar",
                "11122233344",
                "11988880000",
                "Rua A, 10",
                "Sem renda"
        );

        var result = mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoCriacao)))
                .andExpect(status().isCreated())
                .andReturn();

        Number idNumber = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.idBeneficiario");
        Long idBeneficiario = idNumber.longValue();

        BeneficiarioRequestDTO dtoAtualizacao = new BeneficiarioRequestDTO(
                "Lucas Alencar Silva",
                "11122233344",
                "11988889999",
                "Rua B, 20",
                "Empregado recentemente"
        );

        mockMvc.perform(put("/api/v1/beneficiarios/{id}", idBeneficiario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoAtualizacao)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBeneficiario").value(idBeneficiario))
                .andExpect(jsonPath("$.nome").value("Lucas Alencar Silva"))
                .andExpect(jsonPath("$.telefone").value("11988889999"))
                .andExpect(jsonPath("$.endereco").value("Rua B, 20"))
                .andExpect(jsonPath("$.situacaoVulnerabilidade").value("Empregado recentemente"));
    }

    @Test
    @DisplayName("Deve atualizar beneficiário parcialmente com PATCH retornando 200 OK")
    void deveAtualizarBeneficiarioComPatch() throws Exception {
        BeneficiarioRequestDTO dtoCriacao = new BeneficiarioRequestDTO(
                "Fernanda Souza",
                "99988877766",
                "11977776666",
                "Rua Central, 50",
                "Vulnerabilidade"
        );

        var result = mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoCriacao)))
                .andExpect(status().isCreated())
                .andReturn();

        Number idNumber = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.idBeneficiario");
        Long idBeneficiario = idNumber.longValue();

        BeneficiarioPatchDTO patchDTO = new BeneficiarioPatchDTO(
                null,
                "11911110000",
                null,
                null
        );

        mockMvc.perform(patch("/api/v1/beneficiarios/{id}", idBeneficiario)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idBeneficiario").value(idBeneficiario))
                .andExpect(jsonPath("$.nome").value("Fernanda Souza"))
                .andExpect(jsonPath("$.telefone").value("11911110000"));
    }

    @Test
    @DisplayName("Deve deletar beneficiário com 204 No Content e retornar 404 ao buscar após deleção")
    void deveDeletarBeneficiario() throws Exception {
        BeneficiarioRequestDTO dto = new BeneficiarioRequestDTO(
                "Carlos Silva",
                "55544433322",
                "11922223333",
                "Rua do Meio, 45",
                "Vulnerabilidade transitória"
        );

        var result = mockMvc.perform(post("/api/v1/beneficiarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        Number idNumber = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.idBeneficiario");
        Long idBeneficiario = idNumber.longValue();

        mockMvc.perform(delete("/api/v1/beneficiarios/{id}", idBeneficiario))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/beneficiarios/{id}", idBeneficiario))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
