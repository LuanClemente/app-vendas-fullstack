package br.com.seuprojeto.app_vendas.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.seuprojeto.app_vendas.dto.TopClienteDTO;
import br.com.seuprojeto.app_vendas.entities.Venda;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    // Método para buscar vendas de um usuário com paginação
    Page<Venda> findByUsuarioId(Long usuarioId, Pageable pageable);

    // Método para buscar todas as vendas de um cliente (sem paginação)
    List<Venda> findByClienteId(Long idCliente);
    
    // Método para buscar todas as vendas de um usuário (sem paginação)
    List<Venda> findByUsuarioId(Long usuarioId);

    // Consulta para o Dashboard do Cliente
    @Query("SELECT v FROM Venda v WHERE v.cliente.id = :idCliente AND v.dataDaVenda BETWEEN :inicio AND :fim")
    List<Venda> findVendasByClienteAndPeriodo(@Param("idCliente") Long idCliente, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // Consulta para o Dashboard do Vendedor
    @Query("SELECT v FROM Venda v WHERE v.usuario.id = :usuarioId AND v.dataDaVenda BETWEEN :inicio AND :fim")
    List<Venda> findVendasByUsuarioAndPeriodo(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // Consulta para o Ranking de Top 10 Clientes
    @Query("SELECT new br.com.seuprojeto.app_vendas.dto.TopClienteDTO(v.cliente.id, v.cliente.nomeEmpresa, SUM(v.valor)) " +
           "FROM Venda v " +
           "WHERE v.usuario.id = :usuarioId AND v.dataDaVenda BETWEEN :inicio AND :fim " +
           "GROUP BY v.cliente.id, v.cliente.nomeEmpresa " +
           "ORDER BY SUM(v.valor) DESC " +
           "LIMIT 10")
    List<TopClienteDTO> findTop10ClientesByPeriodo(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    
}