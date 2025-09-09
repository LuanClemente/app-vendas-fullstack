package br.com.seuprojeto.app_vendas.repositories;

import java.util.Optional; // <-- IMPORTANTE: Precisamos desta importação

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.seuprojeto.app_vendas.entities.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // ESTA LINHA PRECISA ESTAR ATIVA (SEM AS BARRAS //)
    Optional<Usuario> findByEmail(String email);
    
}