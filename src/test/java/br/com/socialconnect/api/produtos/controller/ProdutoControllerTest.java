package br.com.socialconnect.api.produtos.controller;

import br.com.socialconnect.api.produtos.dto.ProdutoRequestDTO;
import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class ProdutoControllerTest {

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
        jdbcTemplate.update("DELETE FROM produtos");
    }

    @Test
    @DisplayName("Deve cadastrar produto com sucesso retornando 201 e cabeçalho Location")
    void deveCriarProdutoComSucessoRetornando201ELocation() throws Exception {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Arroz 5kg Tipo 1",
                CategoriaProduto.ALIMENTO,
                20,
                10,
                "pacote"
        );

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.idProduto").isNumber())
                .andExpect(jsonPath("$.nome").value("Arroz 5kg Tipo 1"))
                .andExpect(jsonPath("$.categoria").value("ALIMENTO"))
                .andExpect(jsonPath("$.estoqueAtual").value(20))
                .andExpect(jsonPath("$.estoqueMinimo").value(10))
                .andExpect(jsonPath("$.unidadeMedida").value("pacote"))
                .andExpect(jsonPath("$.dataCadastro").isNotEmpty())
                .andExpect(jsonPath("$.estoqueBaixo").value(false));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando nome for vazio ou dados obrigatórios ausentes")
    void deveRetornar400QuandoDadosInvalidosNoCadastro() throws Exception {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "",
                null,
                10,
                5,
                ""
        );

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Erro de validação"))
                .andExpect(jsonPath("$.errors").isArray());
    }

    @Test
    @DisplayName("Deve retornar 409 Conflict quando cadastrar produto com nome duplicado")
    void deveRetornar409QuandoNomeDuplicado() throws Exception {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Feijão Carioca 1kg",
                CategoriaProduto.ALIMENTO,
                15,
                5,
                "pacote"
        );

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.title").value("Nome de produto já cadastrado"));
    }

    @Test
    @DisplayName("Deve retornar 422 Unprocessable Entity quando estoqueAtual for negativo")
    void deveRetornar422QuandoEstoqueAtualNegativo() throws Exception {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Sabonete Líquido",
                CategoriaProduto.HIGIENE,
                -3,
                5,
                "unidade"
        );

        mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.title").value("Estoque inválido"));
    }

    @Test
    @DisplayName("Deve listar produtos com paginação e filtros")
    void deveListarProdutosPaginadosComFiltros() throws Exception {
        ProdutoRequestDTO p1 = new ProdutoRequestDTO("Camiseta Infantil", CategoriaProduto.ROUPA, 8, 10, "unidade");
        ProdutoRequestDTO p2 = new ProdutoRequestDTO("Calça Jeans", CategoriaProduto.ROUPA, 2, 5, "unidade");
        ProdutoRequestDTO p3 = new ProdutoRequestDTO("Creme Dental", CategoriaProduto.HIGIENE, 20, 5, "unidade");

        mockMvc.perform(post("/api/v1/produtos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p1)));
        mockMvc.perform(post("/api/v1/produtos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p2)));
        mockMvc.perform(post("/api/v1/produtos").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(p3)));

        mockMvc.perform(get("/api/v1/produtos?categoria=ROUPA&page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].categoria").value("ROUPA"))
                .andExpect(jsonPath("$.content[1].categoria").value("ROUPA"));
    }

    @Test
    @DisplayName("Deve buscar produto por ID e calcular alerta estoqueBaixo")
    void deveBuscarProdutoPorIdComAlertaEstoqueBaixo() throws Exception {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Óleo de Soja 900ml",
                CategoriaProduto.ALIMENTO,
                2,
                10,
                "garrafa"
        );

        String responseJson = mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long idProduto = objectMapper.readTree(responseJson).get("idProduto").asLong();

        mockMvc.perform(get("/api/v1/produtos/" + idProduto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProduto").value(idProduto))
                .andExpect(jsonPath("$.estoqueBaixo").value(true));
    }

    @Test
    @DisplayName("Deve retornar 404 quando buscar ID inexistente")
    void deveRetornar404QuandoBuscarIdInexistente() throws Exception {
        mockMvc.perform(get("/api/v1/produtos/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Deve atualizar produto por completo com sucesso")
    void deveAtualizarProdutoComSucesso() throws Exception {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Macarrão Espaguete",
                CategoriaProduto.ALIMENTO,
                10,
                5,
                "pacote"
        );

        String responseJson = mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long idProduto = objectMapper.readTree(responseJson).get("idProduto").asLong();

        ProdutoRequestDTO dtoAtualizado = new ProdutoRequestDTO(
                "Macarrão Parafuso",
                CategoriaProduto.ALIMENTO,
                25,
                10,
                "pacote"
        );

        mockMvc.perform(put("/api/v1/produtos/" + idProduto)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoAtualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Macarrão Parafuso"))
                .andExpect(jsonPath("$.estoqueAtual").value(25))
                .andExpect(jsonPath("$.estoqueBaixo").value(false));
    }

    @Test
    @DisplayName("Deve deletar produto com sucesso retornando 204")
    void deveDeletarProdutoComSucesso() throws Exception {
        ProdutoRequestDTO dto = new ProdutoRequestDTO(
                "Item Para Excluir",
                CategoriaProduto.OUTROS,
                5,
                1,
                "unidade"
        );

        String responseJson = mockMvc.perform(post("/api/v1/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long idProduto = objectMapper.readTree(responseJson).get("idProduto").asLong();

        mockMvc.perform(delete("/api/v1/produtos/" + idProduto))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/produtos/" + idProduto))
                .andExpect(status().isNotFound());
    }
}
