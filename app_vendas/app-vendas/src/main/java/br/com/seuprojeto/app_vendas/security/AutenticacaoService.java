package br.com.seuprojeto.app_vendas.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.seuprojeto.app_vendas.repositories.UsuarioRepository;

@Service
public class AutenticacaoService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Este método é chamado pelo Spring Security quando um usuário tenta se autenticar.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Nossa regra de negócio: o "username" é o e-mail.
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
    }
}