package br.com.seuprojeto.app_vendas.dto;

import br.com.seuprojeto.app_vendas.enums.PerfilUsuario;

public record UsuarioRespostaDTO(
    Long id,
    String nome,
    String email,
    PerfilUsuario perfil
) {
}