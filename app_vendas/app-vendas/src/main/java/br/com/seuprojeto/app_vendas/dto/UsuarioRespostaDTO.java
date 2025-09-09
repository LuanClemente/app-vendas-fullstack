package br.com.seuprojeto.app_vendas.dto;

import br.com.seuprojeto.app_vendas.enums.PerfilUsuario;

// Este DTO representa os dados públicos de um usuário.
// Note que não há senha aqui.
public record UsuarioRespostaDTO(
    Long id,
    String nome,
    String email,
    PerfilUsuario perfil // Usando o Enum para consistência
) {
}