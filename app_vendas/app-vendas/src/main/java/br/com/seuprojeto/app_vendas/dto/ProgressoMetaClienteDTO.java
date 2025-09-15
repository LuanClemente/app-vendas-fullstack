package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record ProgressoMetaClienteDTO(
    Long idMeta,
    String nomeCliente,
    String nomeEmpresa,
    BigDecimal valorMeta,
    BigDecimal totalVendido,
    double porcentagemAtingida
) {
}