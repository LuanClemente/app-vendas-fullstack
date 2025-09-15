package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record DashboardClienteDTO(
    BigDecimal totalVendidoMes,
    BigDecimal mediaCompras
) {
}