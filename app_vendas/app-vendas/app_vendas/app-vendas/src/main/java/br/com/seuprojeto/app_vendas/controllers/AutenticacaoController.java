package br.com.seuprojeto.app_vendas.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.seuprojeto.app_vendas.dto.DadosLoginDTO;
import br.com.seuprojeto.app_vendas.dto.TokenJWTDTO;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.security.TokenService;

@RestController
@RequestMapping("/api/login")
public class AutenticacaoController {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    // A CORREÇÃO É AQUI: De ResponseEntity para ResponseEntity<TokenJWTDTO>
    public ResponseEntity<TokenJWTDTO> efetuarLogin(@RequestBody DadosLoginDTO dados) {
        // 1. Cria um "pacote" com os dados de login que o Spring Security entende.
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());

        // 2. Efetua a autenticação. O Spring vai chamar nosso AutenticacaoService,
        // pegar o usuário do banco e comparar as senhas usando o BCrypt.
        // Se as credenciais estiverem erradas, ele lança um erro aqui.
        var authentication = manager.authenticate(authenticationToken);

        // 3. Se o login foi bem-sucedido, pega o objeto do usuário...
        var usuario = (Usuario) authentication.getPrincipal();
        
        // 4. ...e gera o token JWT.
        var tokenJWT = tokenService.gerarToken(usuario);

        // 5. Devolve o token para o cliente com status 200 OK.
        return ResponseEntity.ok(new TokenJWTDTO(tokenJWT, usuario.getId(), usuario.getNome(),usuario.getPerfil()));
}
}