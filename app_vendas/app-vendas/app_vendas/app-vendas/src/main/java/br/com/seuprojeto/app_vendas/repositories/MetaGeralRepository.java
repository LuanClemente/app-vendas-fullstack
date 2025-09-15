package br.com.seuprojeto.app_vendas.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.seuprojeto.app_vendas.entities.MetaGeral;

@Repository
public interface MetaGeralRepository extends JpaRepository<MetaGeral, Long> {

    // Método "mágico" que usaremos para encontrar se já existe uma meta geral
    // para um mês e ano específicos. Isso será útil para a lógica de criar ou atualizar.
    Optional<MetaGeral> findByMesAndAno(int mes, int ano);

}