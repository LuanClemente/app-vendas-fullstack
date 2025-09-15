package br.com.seuprojeto.app_vendas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

// Adicionamos o campo 'faturada' no final
public record VendaRespostaDTO(
    Long id,
    BigDecimal valor,
    LocalDate dataDaVenda,
    String descricao,
    Long idCliente,
    String nomeCliente,
    Long idVendedor,
    String nomeVendedor,
    boolean faturada // <-- ESTE É O CAMPO QUE FALTAVA
) {
}