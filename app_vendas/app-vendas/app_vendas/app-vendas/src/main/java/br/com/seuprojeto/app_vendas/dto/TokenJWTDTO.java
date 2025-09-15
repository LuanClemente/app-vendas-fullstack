package br.com.seuprojeto.app_vendas.dto;

import br.com.seuprojeto.app_vendas.enums.PerfilUsuario;

// Este record representa o JSON que vamos devolver após o login bem-sucedido
public record TokenJWTDTO(String token, Long id, String nome, PerfilUsuario perfil) {
}