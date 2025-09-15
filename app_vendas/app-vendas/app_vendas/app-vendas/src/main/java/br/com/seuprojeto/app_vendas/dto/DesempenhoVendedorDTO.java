package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record DesempenhoVendedorDTO(
    String nomeVendedor,
    BigDecimal valorMeta,
    BigDecimal totalVendido,
    double porcentagemAtingida
) {
}