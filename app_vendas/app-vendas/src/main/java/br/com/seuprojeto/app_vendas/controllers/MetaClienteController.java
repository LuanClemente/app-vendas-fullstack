package br.com.seuprojeto.app_vendas.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.seuprojeto.app_vendas.dto.DadosMetaClienteDTO;
import br.com.seuprojeto.app_vendas.dto.MetaClienteRespostaDTO;
import br.com.seuprojeto.app_vendas.entities.MetaCliente;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.services.MetaClienteService;

@RestController
@RequestMapping("/api/metas-cliente")
public class MetaClienteController {
    
    private final MetaClienteService metaClienteService;

    public MetaClienteController(MetaClienteService metaClienteService) {
        this.metaClienteService = metaClienteService;
    }

    /**
     * Endpoint para definir ou atualizar uma meta para um cliente.
     */
    @PostMapping
    public ResponseEntity<MetaClienteRespostaDTO> definirMeta(@RequestBody DadosMetaClienteDTO dados) {
        MetaCliente metaSalva = metaClienteService.definirMeta(dados);
        
        MetaClienteRespostaDTO respostaDTO = new MetaClienteRespostaDTO(
            metaSalva.getId(),
            metaSalva.getValor(),
            metaSalva.getMes(),
            metaSalva.getAno(),
            metaSalva.getCliente().getId(),
            metaSalva.getCliente().getNomeContato(),
            metaSalva.getUsuario().getId()
        );

        return ResponseEntity.ok(respostaDTO);
    }

    /**
     * Endpoint para listar as metas de clientes definidas por um vendedor em um mês/ano.
     */
    @GetMapping
    public ResponseEntity<List<MetaClienteRespostaDTO>> listarMetasPorVendedor(
            Authentication authentication,
            @RequestParam("mes") int mes,
            @RequestParam("ano") int ano) {
        
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        List<MetaCliente> metas = metaClienteService.listarMetasPorVendedor(usuarioLogado.getId(), mes, ano);

        List<MetaClienteRespostaDTO> respostaDTOs = metas.stream().map(meta -> 
            new MetaClienteRespostaDTO(
                meta.getId(),
                meta.getValor(),
                meta.getMes(),
                meta.getAno(),
                meta.getCliente().getId(),
                meta.getCliente().getNomeContato(),
                meta.getUsuario().getId()
            )
        ).collect(Collectors.toList());

        return ResponseEntity.ok(respostaDTOs);
    }
    @PutMapping("/{id}")
    public ResponseEntity<MetaClienteRespostaDTO> atualizarMeta(
            @PathVariable Long id, 
            @RequestBody BigDecimal novoValor) {
        
        MetaCliente metaAtualizada = metaClienteService.atualizarMeta(id, novoValor);
        // Reutilizamos nosso DTO para a resposta
        MetaClienteRespostaDTO respostaDTO = new MetaClienteRespostaDTO(
            metaAtualizada.getId(), metaAtualizada.getValor(), metaAtualizada.getMes(),
            metaAtualizada.getAno(), metaAtualizada.getCliente().getId(),
            metaAtualizada.getCliente().getNomeContato(), metaAtualizada.getUsuario().getId()
        );
        return ResponseEntity.ok(respostaDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarMeta(@PathVariable Long id) {
        metaClienteService.deletarMeta(id);
        return ResponseEntity.noContent().build();
    }
}