package br.com.seuprojeto.app_vendas.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.seuprojeto.app_vendas.dto.DashboardClienteDTO;
import br.com.seuprojeto.app_vendas.dto.DashboardSupervisorDTO;
import br.com.seuprojeto.app_vendas.dto.DashboardVendedorDTO;
import br.com.seuprojeto.app_vendas.dto.DesempenhoVendedorDTO;
import br.com.seuprojeto.app_vendas.dto.ProgressoMetaClienteDTO;
import br.com.seuprojeto.app_vendas.dto.TopClienteDTO;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.services.RelatorioService;

@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {
    
    private final RelatorioService relatorioService;

    @Autowired
    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<DashboardClienteDTO> getDashboardCliente(@PathVariable Long idCliente) {
        BigDecimal totalMes = relatorioService.calcularTotalVendidoClienteNoMes(idCliente);
        BigDecimal media = relatorioService.calcularMediaComprasCliente(idCliente);
        
        var dto = new DashboardClienteDTO(totalMes, media);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/dashboard/vendedor")
    public ResponseEntity<DashboardVendedorDTO> getDashboardDoVendedor(Authentication authentication) {
        Usuario vendedorLogado = (Usuario) authentication.getPrincipal();
        
        DashboardVendedorDTO dashboard = relatorioService.getDashboardVendedor(vendedorLogado);
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/top-clientes")
    public ResponseEntity<List<TopClienteDTO>> getTopClientesDoMes(Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        List<TopClienteDTO> topClientes = relatorioService.buscarTopClientesDoMes(usuarioLogado);
        return ResponseEntity.ok(topClientes);
    }

    @GetMapping("/progresso-metas-cliente")
    public ResponseEntity<List<ProgressoMetaClienteDTO>> getProgressoMetas(
            Authentication authentication,
            @RequestParam("mes") int mes,
            @RequestParam("ano") int ano) {
        
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        List<ProgressoMetaClienteDTO> progresso = relatorioService.getProgressoMetasClientes(usuarioLogado, mes, ano);
        return ResponseEntity.ok(progresso);
    }

    @GetMapping("/desempenho-equipe")
    public ResponseEntity<List<DesempenhoVendedorDTO>> getDesempenhoEquipe() {
        List<DesempenhoVendedorDTO> desempenho = relatorioService.getDesempenhoDaEquipe();
        return ResponseEntity.ok(desempenho);
    }

    @GetMapping("/dashboard-supervisor")
    public ResponseEntity<DashboardSupervisorDTO> getDashboardSupervisor() {
        DashboardSupervisorDTO dashboard = relatorioService.getDashboardSupervisor();
        return ResponseEntity.ok(dashboard);
    }
}