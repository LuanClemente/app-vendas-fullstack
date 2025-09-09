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

    // 1. MUDANÇA AQUI: Trocamos a injeção de campos por um construtor.
    // Declaramos os campos como 'final' para garantir que sejam inicializados.
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // 2. MUDANÇA AQUI: O construtor recebe as dependências.
    // O Spring automaticamente entende que precisa "injetar" as peças aqui.
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

        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
    }

    public Usuario atualizarUsuario(Long id, Usuario dadosAtualizados) {
        Usuario usuarioExistente = this.buscarPorId(id);

        usuarioExistente.setNome(dadosAtualizados.getNome());
        usuarioExistente.setPerfil(dadosAtualizados.getPerfil());

        return usuarioRepository.save(usuarioExistente);
    }

    public void deletarUsuario(Long id) {
        this.buscarPorId(id);
        usuarioRepository.deleteById(id);
    }
}