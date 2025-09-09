package br.com.seuprojeto.app_vendas.repositories;

import java.util.List;
import java.util.Optional; // Adicione esta importação

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.seuprojeto.app_vendas.entities.MetaCliente;

@Repository
public interface MetaClienteRepository extends JpaRepository<MetaCliente, Long> {

    List<MetaCliente> findByUsuarioIdAndMesAndAno(Long usuarioId, int mes, int ano);

    // =======================================================
    //  MÉTODO QUE FALTAVA ADICIONADO AQUI
    // =======================================================
    Optional<MetaCliente> findByClienteIdAndMesAndAno(Long clienteId, int mes, int ano);

}