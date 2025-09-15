package br.com.seuprojeto.app_vendas.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.seuprojeto.app_vendas.dto.DadosMetaClienteDTO;
import br.com.seuprojeto.app_vendas.entities.MetaCliente;
import br.com.seuprojeto.app_vendas.repositories.MetaClienteRepository;

@Service
public class MetaClienteService {
    
    private final MetaClienteRepository metaClienteRepository;
    private final UsuarioService usuarioService;
    private final ClienteService clienteService;

    public MetaClienteService(MetaClienteRepository metaClienteRepository, UsuarioService usuarioService, ClienteService clienteService) {
        this.metaClienteRepository = metaClienteRepository;
        this.usuarioService = usuarioService;
        this.clienteService = clienteService;
    }

    /**
     * Define ou atualiza a meta para um cliente específico.
     */
    public MetaCliente definirMeta(DadosMetaClienteDTO dados) {
        // Busca as entidades para garantir que existem
        var cliente = clienteService.buscarPorId(dados.idCliente());
        var usuario = usuarioService.buscarPorId(dados.idUsuario());

        // Verifica se a meta já existe
        Optional<MetaCliente> metaExistenteOpt = metaClienteRepository
                .findByClienteIdAndMesAndAno(dados.idCliente(), dados.mes(), dados.ano());
        
        MetaCliente meta;
        if (metaExistenteOpt.isPresent()) {
            // Se existe, atualiza o valor
            meta = metaExistenteOpt.get();
            meta.setValor(dados.valor());
        } else {
            // Se não existe, cria uma nova
            meta = new MetaCliente();
            meta.setCliente(cliente);
            meta.setUsuario(usuario);
            meta.setMes(dados.mes());
            meta.setAno(dados.ano());
            meta.setValor(dados.valor());
        }

        return metaClienteRepository.save(meta);
    }

    /**
     * Lista todas as metas de clientes de um vendedor para um mês/ano.
     */
    public List<MetaCliente> listarMetasPorVendedor(Long usuarioId, int mes, int ano) {
        return metaClienteRepository.findByUsuarioIdAndMesAndAno(usuarioId, mes, ano);
    }
    public MetaCliente atualizarMeta(Long id, BigDecimal novoValor) {
        MetaCliente metaExistente = metaClienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meta do cliente não encontrada."));
        
        metaExistente.setValor(novoValor);
        return metaClienteRepository.save(metaExistente);
    }

    /**
     * Deleta uma meta de cliente.
     */
     /**
     * Atualiza o valor de uma meta de cliente existente.
     */
    /**
     * Deleta uma meta de cliente.
     */
    public void deletarMeta(Long id) {
        if (!metaClienteRepository.existsById(id)) {
            throw new RuntimeException("Meta do cliente não encontrada.");
        }
        metaClienteRepository.deleteById(id);
    }
}