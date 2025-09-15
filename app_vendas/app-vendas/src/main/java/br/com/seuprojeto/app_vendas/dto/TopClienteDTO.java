package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;

public record TopClienteDTO(
    Long clienteId,
    String nomeEmpresa,
    BigDecimal totalComprado
) {
}