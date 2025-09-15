package br.com.seuprojeto.app_vendas.dto;

// Este record representa o JSON que vamos receber no login
public record DadosLoginDTO(String email, String senha) {
}