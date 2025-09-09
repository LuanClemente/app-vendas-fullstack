package br.com.seuprojeto.app_vendas.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.seuprojeto.app_vendas.entities.Meta;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Long> {

    // Magic method! O Spring Data JPA cria a consulta SQL automaticamente
    // apenas pelo nome do método. Ele vai procurar uma meta pelo ID do usuário,
    // pelo mês E pelo ano. Exatamente o que vamos precisar!
    Optional<Meta> findByUsuarioIdAndMesAndAno(Long usuarioId, int mes, int ano);
    List<Meta> findByUsuarioIdOrderByAnoDescMesDesc(Long usuarioId);

}