package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record DadosMetaGeralDTO(
    int mes,
    int ano,
    BigDecimal valor
) {
}