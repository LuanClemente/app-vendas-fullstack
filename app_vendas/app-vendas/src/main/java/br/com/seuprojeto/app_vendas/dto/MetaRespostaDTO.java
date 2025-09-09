package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record MetaRespostaDTO(
    Long id,
    BigDecimal valor,
    int mes,
    int ano,
    Long idUsuario
) {
}