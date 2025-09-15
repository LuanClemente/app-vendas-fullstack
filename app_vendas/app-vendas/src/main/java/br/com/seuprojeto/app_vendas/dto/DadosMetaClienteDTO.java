package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record DadosMetaClienteDTO(
    BigDecimal valor,
    int mes,
    int ano,
    Long idCliente,
    Long idUsuario
) {
}