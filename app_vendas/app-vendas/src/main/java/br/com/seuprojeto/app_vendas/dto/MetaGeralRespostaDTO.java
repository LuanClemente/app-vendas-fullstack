package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record MetaGeralRespostaDTO(
    Long id,
    int mes,
    int ano,
    BigDecimal valor
) {
}