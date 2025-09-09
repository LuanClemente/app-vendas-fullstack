package br.com.seuprojeto.app_vendas.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.seuprojeto.app_vendas.dto.DadosMetaDTO;
import br.com.seuprojeto.app_vendas.entities.Meta;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.repositories.MetaRepository;

@Service
public class MetaService {

    private final MetaRepository metaRepository;
    private final UsuarioService usuarioService;

    public MetaService(MetaRepository metaRepository, UsuarioService usuarioService) {
        this.metaRepository = metaRepository;
        this.usuarioService = usuarioService;
    }

    /**
     * Define ou atualiza a meta de um vendedor para um mês/ano específico.
     */
    public Meta definirMeta(DadosMetaDTO dados) {
        // 1. Busca o usuário correspondente.
        var usuario = usuarioService.buscarPorId(dados.idUsuario());

        // 2. Verifica se já existe uma meta para este usuário neste mês e ano.
        //    Aqui usamos o método "mágico" que criamos no repository!
        Optional<Meta> metaExistenteOpt = metaRepository.findByUsuarioIdAndMesAndAno(
            dados.idUsuario(), dados.mes(), dados.ano());

        Meta meta;
        if (metaExistenteOpt.isPresent()) {
            // 3a. Se a meta já existe, nós a atualizamos.
            meta = metaExistenteOpt.get();
            meta.setValor(dados.valor());
        } else {
            // 3b. Se não existe, criamos uma nova.
            meta = new Meta();
            meta.setUsuario(usuario);
            meta.setMes(dados.mes());
            meta.setAno(dados.ano());
            meta.setValor(dados.valor());
        }

        // 4. Salvamos a meta (seja ela nova ou atualizada) no banco.
        return metaRepository.save(meta);
    }
public List<Meta> listarMetasPorUsuario(Usuario usuario) {
        return metaRepository.findByUsuarioIdOrderByAnoDescMesDesc(usuario.getId());
    }
}