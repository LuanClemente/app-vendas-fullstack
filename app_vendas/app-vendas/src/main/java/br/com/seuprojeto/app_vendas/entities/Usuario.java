package br.com.seuprojeto.app_vendas.entities;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// Novas importações para o Enum
import br.com.seuprojeto.app_vendas.enums.PerfilUsuario;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true)
    private String email;

    private String senha;

    // =======================================================
    //  MUDANÇA PRINCIPAL: Trocamos String por PerfilUsuario
    // =======================================================
    @Enumerated(EnumType.STRING) // Diz ao JPA para salvar o NOME do enum ("VENDEDOR") no banco
    private PerfilUsuario perfil;


    // =================================================================
    //  MUDANÇA NA LÓGICA DE AUTORIDADES PARA USAR OS PERFIS DO ENUM
    // =================================================================
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.perfil == PerfilUsuario.ADMIN) {
            // Admins têm permissão de Admin e de Usuário
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        } else if (this.perfil == PerfilUsuario.SUPERVISOR) {
            // Supervisores têm permissão de Supervisor e de Usuário
            return List.of(new SimpleGrantedAuthority("ROLE_SUPERVISOR"), new SimpleGrantedAuthority("ROLE_USER"));
        } else {
            // Vendedores têm apenas a permissão de Usuário
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    // Métodos de status da conta (sem alteração)
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}