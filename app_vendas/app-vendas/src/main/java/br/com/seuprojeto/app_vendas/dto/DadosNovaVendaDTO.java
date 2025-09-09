package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

// Este record representa o JSON que vamos receber para criar uma nova venda.
public record DadosNovaVendaDTO(
    BigDecimal valor,
    String descricao,
    Long idCliente,
    Long idUsuario
) {
}