package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record DadosNovaVendaDTO(
    BigDecimal valor,
    String descricao,
    Long idCliente,
    Long idUsuario
) {
}