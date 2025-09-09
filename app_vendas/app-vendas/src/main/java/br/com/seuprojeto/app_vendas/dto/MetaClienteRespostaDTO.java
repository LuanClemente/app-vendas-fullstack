package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record MetaClienteRespostaDTO(
    Long id,
    BigDecimal valor,
    int mes,
    int ano,
    Long idCliente,
    String nomeCliente,
    Long idUsuario
) {
}