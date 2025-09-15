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

    // --- MÉTODOS PARA PAGINAÇÃO E LISTAS ---
    Page<Venda> findByUsuarioId(Long usuarioId, Pageable pageable);
    List<Venda> findByClienteId(Long idCliente);
    List<Venda> findByUsuarioId(Long usuarioId);

    // --- MÉTODOS PARA RELATÓRIOS ---
    @Query("SELECT v FROM Venda v WHERE v.cliente.id = :idCliente AND v.dataDaVenda BETWEEN :inicio AND :fim")
    List<Venda> findVendasByClienteAndPeriodo(@Param("idCliente") Long idCliente, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    @Query("SELECT v FROM Venda v WHERE v.usuario.id = :usuarioId AND v.dataDaVenda BETWEEN :inicio AND :fim")
    List<Venda> findVendasByUsuarioAndPeriodo(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // NOVO MÉTODO para buscar TODAS as vendas em um período (para o Dashboard do Supervisor)
    List<Venda> findAllByDataDaVendaBetween(LocalDate inicio, LocalDate fim);

    @Query("SELECT new br.com.seuprojeto.app_vendas.dto.TopClienteDTO(v.cliente.id, v.cliente.nomeEmpresa, SUM(v.valor)) " +
           "FROM Venda v " +
           "WHERE v.usuario.id = :usuarioId AND v.dataDaVenda BETWEEN :inicio AND :fim " +
           "GROUP BY v.cliente.id, v.cliente.nomeEmpresa " +
           "ORDER BY SUM(v.valor) DESC " +
           "LIMIT 10")
    List<TopClienteDTO> findTop10ClientesByPeriodo(@Param("usuarioId") Long usuarioId, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);
    
}