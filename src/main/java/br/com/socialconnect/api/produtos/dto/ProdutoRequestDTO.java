package br.com.socialconnect.api.produtos.dto;

import br.com.socialconnect.api.produtos.model.CategoriaProduto;
import br.com.socialconnect.api.validation.EstoqueNaoNegativo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro e atualização de produto")
public record ProdutoRequestDTO(
        @Schema(description = "Nome do produto", example = "Arroz 5kg")
        @NotBlank(message = "{produto.nome.obrigatorio}")
        @Size(max = 150)
        String nome,

        @Schema(description = "Categoria do produto", example = "ALIMENTO")
        @NotNull(message = "{produto.categoria.obrigatoria}")
        CategoriaProduto categoria,

        @Schema(description = "Quantidade atual em estoque", example = "10")
        @Min(value = 0, message = "{produto.estoque.minimo}")
        @EstoqueNaoNegativo
        Integer estoqueAtual,

        @Schema(description = "Quantidade mínima recomendada para o estoque", example = "5")
        @Min(value = 0, message = "{produto.estoque.minimo}")
        @EstoqueNaoNegativo
        Integer estoqueMinimo,

        @Schema(description = "Unidade de medida do produto", example = "unidade")
        @NotBlank
        @Size(max = 20)
        String unidadeMedida
) {}
