package br.com.seuprojeto.app_vendas.dto;

// Um 'record' é uma forma compacta de criar uma classe imutável.
// Esta linha sozinha cria uma classe com os campos 'mensagem' e 'status',
// construtor, getters e outros métodos necessários.
public record ErroRespostaDTO(String mensagem, int status) {
}