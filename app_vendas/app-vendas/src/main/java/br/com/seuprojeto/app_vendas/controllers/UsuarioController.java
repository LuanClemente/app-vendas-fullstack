package br.com.seuprojeto.app_vendas.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.seuprojeto.app_vendas.dto.UsuarioRespostaDTO;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioRespostaDTO> criarUsuario(@RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioService.criarUsuario(usuario);
        UsuarioRespostaDTO respostaDTO = new UsuarioRespostaDTO(
            novoUsuario.getId(), 
            novoUsuario.getNome(), 
            novoUsuario.getEmail(), 
            novoUsuario.getPerfil()
        );
        return ResponseEntity.status(201).body(respostaDTO);
    }

    @GetMapping
    public List<UsuarioRespostaDTO> buscarTodosUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        
        return usuarios.stream()
                .map(u -> new UsuarioRespostaDTO(
                    u.getId(), 
                    u.getNome(), 
                    u.getEmail(), 
                    u.getPerfil())) // <-- Ordem corrigida
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        UsuarioRespostaDTO respostaDTO = new UsuarioRespostaDTO(
            usuario.getId(), 
            usuario.getNome(), 
            usuario.getEmail(), 
            usuario.getPerfil()
        );
        return ResponseEntity.ok(respostaDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioRespostaDTO> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario dadosUsuario) {
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(id, dadosUsuario);
        UsuarioRespostaDTO respostaDTO = new UsuarioRespostaDTO(
            usuarioAtualizado.getId(),
            usuarioAtualizado.getNome(),
            usuarioAtualizado.getEmail(),
            usuarioAtualizado.getPerfil()
        );
        return ResponseEntity.ok(respostaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}