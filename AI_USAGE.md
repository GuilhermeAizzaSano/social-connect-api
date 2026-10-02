# Registro de Uso de IA Generativa

> **Política da disciplina:** O uso de IA generativa é permitido como
> assistente. O discente é **integralmente responsável** por testar, auditar e
> defender todo o código entregue, independentemente de como foi gerado.

## Instruções

Para cada aula ou entrega, registre abaixo:
- **Data**
- **Ferramenta** (ChatGPT, Copilot, Claude, etc.)
- **Prompt(s) utilizado(s)** (resumo ou cópia)
- **O que foi feito com a saída** (copiado integralmente, adaptado, usado como referência, descartado)

---

## Registro

| Data | Aula | Ferramenta | Prompt (resumo) | Uso da saída |
|------|------|------------|-----------------|--------------|
| 04/09/2026 | Aula 05 | Antigravity (Gemini 3.8 Flash) | "faça um teste automatizado para validar a implementação desse crud e armazene as responses em um json." | Criado teste automatizado DoacaoControllerTest validando fluxo CRUD completo e exportando as responses HTTP para doacoes-crud-responses.json |
| 11/09/2026 | Aula 06 | Antigravity (Gemini 3.8 Flash) | "Faça uma validação se o openapi e o swagger estão funcionais e interativos. Caso não, aponte o que falta e ou o que está quebrado." | Criado teste automatizado OpenApiSwaggerTest validando /api-docs e Swagger UI (/swagger-ui.html e /swagger-ui/index.html), criado OpenApiConfig para dados da API e vinculados schemas RFC 7807 (ProblemDetail) com exemplos nos endpoints. |
| 02/10/2026 | Aula 07 | Antigravity (Gemini 3.8 Flash) | Checklist de Validação de Endpoints REST: Método HTTP correto, Status Codes adequados, Formato JSON, Nomenclatura no plural, camelCase, Envelope de sucesso, Erro RFC 7807, Datas ISO 8601 | Auditoria e adequação do sistema aos padrões REST.|
| 02/10/2026 | Avaliação 1 | Antigravity (Gemini 3.8 Flash) | "Como eu valido todos os testes solicitados na atividade para garantir a nota máxima?" | Execução e auditoria dos testes unitários com Mockito e padrão AAA (ProdutoServiceTest), testes de integração de API via MockMvc (ProdutoControllerTest), validação de contrato OpenAPI/Swagger e configuração de testes com Testcontainers PostgreSQL 17 (ProdutoControllerIntegrationTest) via Maven CLI. |


---

_Declaração: Ao submeter este repositório, confirmo que todo o código foi
revisado, testado e compreendido por mim._