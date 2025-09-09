package br.com.seuprojeto.app_vendas.services;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import br.com.seuprojeto.app_vendas.dto.DadosNovaVendaDTO;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.entities.Venda;
import br.com.seuprojeto.app_vendas.enums.PerfilUsuario;
import br.com.seuprojeto.app_vendas.repositories.VendaRepository;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;

    @Autowired
    public VendaService(VendaRepository vendaRepository, UsuarioService usuarioService, ClienteService clienteService) {
        this.vendaRepository = vendaRepository;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
    }

    public Venda registrarVenda(DadosNovaVendaDTO dados) {
        var cliente = clienteService.buscarPorId(dados.idCliente());
        var usuario = usuarioService.buscarPorId(dados.idUsuario());

        var novaVenda = new Venda();
        novaVenda.setValor(dados.valor());
        novaVenda.setDescricao(dados.descricao());
        novaVenda.setDataDaVenda(LocalDate.now());
        
        novaVenda.setCliente(cliente);
        novaVenda.setUsuario(usuario);

        return vendaRepository.save(novaVenda);
    }

    public Page<Venda> listarTodas(Usuario usuarioLogado, int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by("dataDaVenda").descending());

        if (usuarioLogado.getPerfil() == PerfilUsuario.SUPERVISOR || usuarioLogado.getPerfil() == PerfilUsuario.ADMIN) {
            return vendaRepository.findAll(pageable);
        } else {
            // CORREÇÃO AQUI: Chamando o método correto 'findByUsuarioId' com o ID do usuário
            return vendaRepository.findByUsuarioId(usuarioLogado.getId(), pageable);
        }
    }

    public void deletarVenda(Long id) {
        vendaRepository.findById(id).orElseThrow(() -> new RuntimeException("Venda não encontrada com o ID: " + id));
        vendaRepository.deleteById(id);
    }

    public Venda faturarVenda(Long id) {
        Venda venda = vendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venda não encontrada com o ID: " + id));
        
        venda.setFaturada(true);

        return vendaRepository.save(venda);
    }
}