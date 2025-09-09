package br.com.seuprojeto.app_vendas.dto;

// Este record representa o JSON que vamos devolver após o login bem-sucedido
public record TokenJWTDTO(String token, Long id, String nome) {
}