package br.com.seuprojeto.app_vendas.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.seuprojeto.app_vendas.entities.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    // A anotação @Value injeta valores do nosso application.properties.
    // Aqui, estamos pegando nossa chave secreta.
    @Value("${api.security.token.secret}")
    private String secret;

    /**
     * Método responsável por gerar o Token JWT para um usuário.
     */
    public String gerarToken(Usuario usuario) {
        // Pega a chave secreta e a converte para um formato seguro para criptografia.
        SecretKey chave = getChaveDeAssinatura();
        
        // Define o tempo de expiração do token (2 horas a partir de agora).
        Instant instanteExpiracao = LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));

        // Constrói o token com suas informações (claims).
        return Jwts.builder()
            .issuer("API App Vendas") // Quem está emitindo o token
            .subject(usuario.getEmail()) // A quem o token pertence (o usuário)
            .issuedAt(new Date()) // Data de emissão
            .expiration(Date.from(instanteExpiracao)) // Data de expiração
            .signWith(chave) // Assina o token com nossa chave secreta
            .compact(); // Compacta tudo em uma string
    }

    /**
     * Método responsável por validar o token e extrair o 'subject' (email do usuário).
     */
    public String getSubject(String tokenJWT) {
        try {
            SecretKey chave = getChaveDeAssinatura();
            
            return Jwts.parser()
                .verifyWith(chave) // Verifica se a assinatura do token é válida usando nossa chave
                .build()
                .parseSignedClaims(tokenJWT)
                .getPayload()
                .getSubject(); // Se tudo estiver certo, extrai o subject (email)
        } catch (Exception e) {
            // Se o token estiver expirado, inválido ou nulo, lança um erro.
            throw new RuntimeException("Token JWT inválido ou expirado!");
        }
    }

    /**
     * Converte a string 'secret' em uma chave criptográfica segura.
     */
    private SecretKey getChaveDeAssinatura() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}