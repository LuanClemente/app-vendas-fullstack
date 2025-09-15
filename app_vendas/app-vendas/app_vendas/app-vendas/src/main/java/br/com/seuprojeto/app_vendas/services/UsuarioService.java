package br.com.seuprojeto.app_vendas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.repositories.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario criarUsuario(Usuario usuario) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("Este e-mail já está em uso.");
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    // =======================================================
    //  MÉTODO ATUALIZADO COM A LÓGICA COMPLETA
    // =======================================================
    public Usuario atualizarUsuario(Long id, Usuario dadosAtualizados) {
        Usuario usuarioExistente = this.buscarPorId(id);

        // Validação de e-mail duplicado (a mesma que usamos para clientes)
        Optional<Usuario> usuarioComEmail = usuarioRepository.findByEmail(dadosAtualizados.getEmail());
        if (usuarioComEmail.isPresent() && !usuarioComEmail.get().getId().equals(id)) {
            throw new RuntimeException("O e-mail informado já está em uso por outro usuário.");
        }

        // Agora sim, atualizamos os campos principais
        usuarioExistente.setNome(dadosAtualizados.getNome());
        usuarioExistente.setEmail(dadosAtualizados.getEmail());
        usuarioExistente.setPerfil(dadosAtualizados.getPerfil());

        // Lógica para a senha: só atualiza se uma nova senha for enviada (não estiver vazia)
        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isBlank()) {
            usuarioExistente.setSenha(passwordEncoder.encode(dadosAtualizados.getSenha()));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public void deletarUsuario(Long id) {
        this.buscarPorId(id);
        usuarioRepository.deleteById(id);
    }
}