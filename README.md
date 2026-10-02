# SocialConnect API

> API RESTful de gestão para instituições sociais (ONGs, bancos de alimentos,
> CRAS, abrigos). Conecta doadores, voluntários e beneficiários.

**Disciplina:** Tópicos Especiais em Sistemas para Internet III
**Stack:** Java 21 · Spring Boot 4.1.1 · JPA · H2 (dev) · PostgreSQL (prod)

---

## Como Rodar

### Pré-requisitos

- JDK 21 LTS ([Adoptium](https://adoptium.net/))
- Maven 3.9+ (ou use o wrapper: `./mvnw`)
- IDE: IntelliJ IDEA (recomendado) ou VS Code

### Passos

```bash
# 1. Clone o repositório
git clone <url-do-repo>
cd socialconnect-api

# 2. Compile o projeto
./mvnw clean compile

# 3. Rode os testes automatizados
./mvnw test

# 4. Rode a aplicação
./mvnw spring-boot:run
```

---

## Documentação da API (Swagger / OpenAPI)

Com a aplicação em execução, acesse a documentação interativa pelo Swagger UI:
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## Módulo de Produtos (Controle de Estoque)

Endpoints disponíveis para controle de itens e doações físicas recebidas:

| Método | Endpoint | Descrição | Status de Sucesso | Possíveis Erros |
| :--- | :--- | :--- | :---: | :---: |
| `GET` | `/api/v1/produtos` | Lista produtos paginados (filtros: `nome`, `categoria`) | `200 OK` | `400` |
| `GET` | `/api/v1/produtos/{id_produto}` | Busca produto por ID com alerta de estoque baixo | `200 OK` | `404` |
| `POST` | `/api/v1/produtos` | Cadastra novo produto | `201 Created` | `400`, `409`, `422` |
| `PUT` | `/api/v1/produtos/{id_produto}` | Atualização completa dos dados do produto | `200 OK` | `400`, `404`, `409`, `422` |
| `DELETE` | `/api/v1/produtos/{id_produto}` | Exclui produto cadastrado | `204 No Content` | `404` |

### Regras de Negócio e Tratamento de Erros (RFC 7807)
- **Estoque Não Negativo:** Qualquer operação que resulte em `estoqueAtual < 0` retorna `422 Unprocessable Entity` com Problem Details.
- **Alerta de Estoque Baixo:** O campo computado `estoqueBaixo` no `ProdutoResponseDTO` é `true` quando `estoqueAtual < estoqueMinimo`.
- **Nome Único:** Não é permitido duplicidade de nomes de produtos (`409 Conflict`).
- **Validações de Entrada:** Campos obrigatórios e restrições de tamanho/formato retornam `400 Bad Request`.