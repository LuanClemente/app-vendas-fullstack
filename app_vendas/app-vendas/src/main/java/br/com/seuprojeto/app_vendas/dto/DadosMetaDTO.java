package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record DadosMetaDTO(
    BigDecimal valor,
    int mes,
    int ano,
    Long idUsuario
) {
}