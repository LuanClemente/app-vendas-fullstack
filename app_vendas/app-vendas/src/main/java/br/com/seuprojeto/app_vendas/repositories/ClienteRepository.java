package br.com.seuprojeto.app_vendas.repositories;

import java.util.List; // <-- GARANTA QUE ESTA IMPORTAÇÃO ESTÁ AQUI
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.seuprojeto.app_vendas.entities.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByEmail(String email);

    @Query("SELECT c FROM Cliente c WHERE MONTH(c.dataDeAniversario) = :mes AND DAY(c.dataDeAniversario) = :dia")
    List<Cliente> findAniversariantesDoDia(@Param("mes") int mes, @Param("dia") int dia);
    List<Cliente> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT c FROM Cliente c WHERE LOWER(c.nomeContato) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(c.nomeEmpresa) LIKE LOWER(CONCAT('%', :termo, '%'))")
    List<Cliente> pesquisarPorTermo(@Param("termo") String termo);
}