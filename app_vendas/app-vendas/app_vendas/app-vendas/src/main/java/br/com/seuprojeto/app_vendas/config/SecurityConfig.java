package br.com.seuprojeto.app_vendas.config;

import java.util.Arrays; // importação do arrays necessária para configurar o CORS

import org.springframework.beans.factory.annotation.Autowired; // importação do Autowired necessária para injeção de dependência
import org.springframework.context.annotation.Bean; // importação do Bean necessária para definir beans de configuração
import org.springframework.context.annotation.Configuration; // importação do Configuration necessária para marcar a classe como fonte de definições de bean
import org.springframework.http.HttpMethod; // importação do HttpMethod necessária para definir métodos HTTP
import org.springframework.security.authentication.AuthenticationManager; // importação do AuthenticationManager necessária para gerenciar autenticações
import org.springframework.security.config.Customizer; // importação do Customizer necessária para personalizar configurações
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // importação do AuthenticationConfiguration necessária para configurar autenticação
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // importação do HttpSecurity necessária para configurar segurança HTTP
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // importação do EnableWebSecurity necessária para habilitar a segurança web
import org.springframework.security.config.http.SessionCreationPolicy; // importação do SessionCreationPolicy necessária para definir políticas de criação de sessão
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // importação do BCryptPasswordEncoder necessária para codificação de senhas
import org.springframework.security.crypto.password.PasswordEncoder; // importação do PasswordEncoder necessária para codificação de senhas
import org.springframework.security.web.SecurityFilterChain; // importação do SecurityFilterChain necessária para definir a cadeia de filtros de segurança
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // importação do UsernamePasswordAuthenticationFilter necessária para filtrar autenticações baseadas em nome de usuário e senha
import org.springframework.web.cors.CorsConfiguration; // importação do CorsConfiguration necessária para configurar CORS
import org.springframework.web.cors.CorsConfigurationSource; // importação do CorsConfigurationSource necessária para definir a fonte de configuração CORS
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // importação do UrlBasedCorsConfigurationSource necessária para configurar CORS baseado em URL

import br.com.seuprojeto.app_vendas.security.SecurityFilter; // importação do SecurityFilter personalizada para filtrar requisições

@Configuration // Marca a classe como uma fonte de definições de bean
@EnableWebSecurity // Habilita a segurança web na aplicação

// Configuração de segurança da aplicação//
public class SecurityConfig { // Classe de configuração de segurança

    @Autowired // Injeção de dependência do filtro de segurança personalizado
    private SecurityFilter securityFilter; // Filtro de segurança personalizado

    @Bean // Define o bean para a cadeia de filtros de segurança
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // Configura a segurança HTTP
        return http // Configuração da segurança HTTP
            .csrf(csrf -> csrf.disable()) // Desabilita a proteção CSRF
            .cors(Customizer.withDefaults()) // Habilita o CORS com configurações padrão
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Define a política de criação de sessão como stateless
            .authorizeHttpRequests(auth -> auth // Configura as regras de autorização
                // Rotas Públicas 
                .requestMatchers(HttpMethod.POST, "/api/login").permitAll() // Permite acesso público à rota de login
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll() // Permite acesso público à rota de cadastro de usuário
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permite requisições OPTIONS para todas as rotas
                
                // Rotas de Supervisor/Admin
                .requestMatchers(HttpMethod.GET, "/api/usuarios").hasAnyRole("SUPERVISOR", "ADMIN") // Permite acesso apenas a supervisores e admins para listar usuários
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAnyRole("SUPERVISOR", "ADMIN") // Permite acesso apenas a supervisores e admins para atualizar usuários
                .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasAnyRole("SUPERVISOR", "ADMIN") // Permite acesso apenas a supervisores e admins para deletar usuários
                .requestMatchers("/api/relatorios/desempenho-equipe").hasAnyRole("SUPERVISOR", "ADMIN") // Permite acesso apenas a supervisores e admins para relatórios de desempenho da equipe
                .requestMatchers("/api/metas-gerais/**").hasAnyRole("SUPERVISOR", "ADMIN") // Permite acesso apenas a supervisores e admins para metas gerais
                
                // Qualquer outra requisição precisa apenas de autenticação
                .anyRequest().authenticated() // Permite qualquer outra requisição autenticada
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class) // Adiciona o filtro de segurança personalizado antes do filtro de autenticação padrão
            .build(); // Constrói a cadeia de filtros de segurança
    } // fim do método securityFilterChain
    
    // Configuração do CORS //

    @Bean // Define o bean para a fonte de configuração CORS
    CorsConfigurationSource corsConfigurationSource() { // Configura a fonte de configuração CORS
        CorsConfiguration configuration = new CorsConfiguration(); // Cria uma nova configuração CORS
        configuration.setAllowedOrigins(Arrays.asList("*")); // Permite todas as origens
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS")); // Permite os métodos HTTP especificados
        configuration.setAllowedHeaders(Arrays.asList("*")); // Permite todos os cabeçalhos
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // Cria a fonte de configuração baseada em URL
        source.registerCorsConfiguration("/**", configuration); // Registra a configuração para todas as rotas
        return source; // Retorna a fonte de configuração CORS
    }

    // Configuração de Autenticação //

    @Bean // Define o bean para o gerenciador de autenticação
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception { // Configura o gerenciador de autenticação
        return configuration.getAuthenticationManager(); // Retorna o gerenciador de autenticação configurado
    }

    // Configuração de Codificação de Senhas //

    @Bean // Define o bean para a codificação de senhas
    public PasswordEncoder passwordEncoder() { // Configura o codificador de senhas
        return new BCryptPasswordEncoder(); // Retorna uma instância do codificador BCrypt
    }
}

