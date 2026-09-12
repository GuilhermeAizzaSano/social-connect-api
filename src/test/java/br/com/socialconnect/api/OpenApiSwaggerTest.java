package br.com.socialconnect.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "springdoc.api-docs.enabled=true",
        "springdoc.swagger-ui.enabled=true"
})
class OpenApiSwaggerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("OpenAPI /api-docs deve estar funcional, retornar 200 e conter endpoints, tags e schemas com exemplos")
    void apiDocsDeveEstarFuncional() throws Exception {
        mockMvc.perform(get("/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openapi").isNotEmpty())
                .andExpect(jsonPath("$.info.title").value("SocialConnect API"))
                .andExpect(jsonPath("$.paths['/api/v1/beneficiarios']").exists())
                .andExpect(jsonPath("$.paths['/api/v1/doacoes']").exists())
                .andExpect(jsonPath("$.components.schemas['BeneficiarioRequestDTO']").exists())
                .andExpect(jsonPath("$.components.schemas['BeneficiarioResponseDTO']").exists())
                .andExpect(jsonPath("$.components.schemas['DoacaoRequestDTO']").exists())
                .andExpect(jsonPath("$.components.schemas['DoacaoResponseDTO']").exists())
                .andExpect(jsonPath("$.components.schemas['ProblemDetail']").exists());
    }

    @Test
    @DisplayName("Swagger UI deve estar funcional e acessível em /swagger-ui/index.html")
    void swaggerUiDeveEstarAcessivel() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Swagger UI")));
    }

    @Test
    @DisplayName("Swagger UI /swagger-ui.html deve redirecionar corretamente")
    void swaggerUiHtmlDeveRedirecionar() throws Exception {
        mockMvc.perform(get("/swagger-ui.html"))
                .andExpect(status().is3xxRedirection());
    }
}
