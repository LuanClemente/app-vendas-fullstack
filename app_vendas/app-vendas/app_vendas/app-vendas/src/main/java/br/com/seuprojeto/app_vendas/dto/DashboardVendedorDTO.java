package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record DashboardVendedorDTO(
    BigDecimal valorMeta,
    BigDecimal totalVendido,
    BigDecimal valorFaltante,
    double porcentagemAtingida,
    BigDecimal totalCortes,
    // ===============================================
    // CAMPOS ADICIONADOS PARA A NOVA FUNCIONALIDADE
    // ===============================================
    BigDecimal totalFaturado,
    double porcentagemFaturada
) {
}