package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record DashboardSupervisorDTO(
    BigDecimal metaGeral,
    BigDecimal totalVendidoGeral,
    BigDecimal totalFaturadoGeral,
    BigDecimal totalCortesGeral,
    BigDecimal faltaParaFaturarGeral,
    BigDecimal faltaParaMetaGeral,
    double porcentagemGeralAtingida,
    double porcentagemGeralFaturada
) {
}