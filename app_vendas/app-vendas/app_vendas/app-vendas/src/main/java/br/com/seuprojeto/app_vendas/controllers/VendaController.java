package br.com.seuprojeto.app_vendas.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.seuprojeto.app_vendas.dto.DadosNovaVendaDTO;
import br.com.seuprojeto.app_vendas.dto.VendaRespostaDTO;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.entities.Venda;
import br.com.seuprojeto.app_vendas.services.VendaService;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService vendaService;

    public VendaController(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    @PostMapping
    public ResponseEntity<VendaRespostaDTO> registrarVenda(@RequestBody DadosNovaVendaDTO dados) {
        Venda novaVenda = vendaService.registrarVenda(dados);
        VendaRespostaDTO respostaDTO = new VendaRespostaDTO(
            novaVenda.getId(), novaVenda.getValor(), novaVenda.getDataDaVenda(), 
            novaVenda.getDescricao(), novaVenda.getCliente().getId(), 
            novaVenda.getCliente().getNomeContato(), novaVenda.getUsuario().getId(), 
            novaVenda.getUsuario().getNome(), novaVenda.isFaturada()
        );
        return ResponseEntity.status(201).body(respostaDTO);
    }

    @GetMapping
    public ResponseEntity<Page<VendaRespostaDTO>> listarTodasVendas(
            Authentication authentication,
            @RequestParam(name = "pagina", defaultValue = "0") int pagina) {
        
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        Page<Venda> paginaDeVendas = vendaService.listarTodas(usuarioLogado, pagina);
        
        Page<VendaRespostaDTO> paginaDeDTOs = paginaDeVendas.map(venda -> 
            new VendaRespostaDTO(
                venda.getId(), venda.getValor(), venda.getDataDaVenda(), 
                venda.getDescricao(), venda.getCliente().getId(), 
                venda.getCliente().getNomeContato(), venda.getUsuario().getId(), 
                venda.getUsuario().getNome(), venda.isFaturada()
            )
        );

        return ResponseEntity.ok(paginaDeDTOs);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVenda(@PathVariable Long id) {
        vendaService.deletarVenda(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/faturar")
    public ResponseEntity<VendaRespostaDTO> faturarVenda(@PathVariable Long id) {
        Venda vendaFaturada = vendaService.faturarVenda(id);
        VendaRespostaDTO respostaDTO = new VendaRespostaDTO(
            vendaFaturada.getId(), vendaFaturada.getValor(), vendaFaturada.getDataDaVenda(), 
            vendaFaturada.getDescricao(), vendaFaturada.getCliente().getId(), 
            vendaFaturada.getCliente().getNomeContato(), vendaFaturada.getUsuario().getId(), 
            vendaFaturada.getUsuario().getNome(), vendaFaturada.isFaturada()
        );
        return ResponseEntity.ok(respostaDTO);
    }
}