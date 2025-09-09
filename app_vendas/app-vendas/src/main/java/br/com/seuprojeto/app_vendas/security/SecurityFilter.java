package br.com.seuprojeto.app_vendas.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.seuprojeto.app_vendas.repositories.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        System.out.println("\n--- [INÍCIO DO FILTRO DE SEGURANÇA] ---");
        System.out.println("Recebendo requisição para: " + request.getMethod() + " " + request.getRequestURI());

        var tokenJWT = recuperarToken(request);

        if (tokenJWT != null) {
            System.out.println("TOKEN JWT ENCONTRADO: " + tokenJWT.substring(0, 15) + "..."); // Mostra só o começo do token
            try {
                var subject = tokenService.getSubject(tokenJWT);
                System.out.println("VALIDAÇÃO DO TOKEN: Sucesso. Subject (email): " + subject);
                
                var usuario = usuarioRepository.findByEmail(subject)
                    .orElseThrow(() -> new RuntimeException("Usuário do token não encontrado no banco."));
                System.out.println("BUSCA NO BANCO: Usuário encontrado: " + usuario.getEmail());

                var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("AUTENTICAÇÃO: Usuário definido no contexto de segurança.");

            } catch (Exception e) {
                System.err.println("ERRO NA VALIDAÇÃO DO TOKEN: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        } else {
            System.out.println("NENHUM TOKEN JWT ENCONTRADO no cabeçalho 'Authorization'.");
        }

        filterChain.doFilter(request, response);
        System.out.println("--- [FIM DO FILTRO DE SEGURANÇA] ---\n");
    }

    private String recuperarToken(HttpServletRequest request) {
        var authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}