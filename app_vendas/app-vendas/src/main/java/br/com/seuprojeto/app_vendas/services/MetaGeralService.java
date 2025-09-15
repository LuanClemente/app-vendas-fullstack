package br.com.seuprojeto.app_vendas.services;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.seuprojeto.app_vendas.dto.DadosMetaGeralDTO;
import br.com.seuprojeto.app_vendas.entities.MetaGeral;
import br.com.seuprojeto.app_vendas.repositories.MetaGeralRepository;

@Service
public class MetaGeralService {

    private final MetaGeralRepository metaGeralRepository;

    public MetaGeralService(MetaGeralRepository metaGeralRepository) {
        this.metaGeralRepository = metaGeralRepository;
    }

    /**
     * Define ou atualiza a meta geral para um mês/ano específico.
     */
    public MetaGeral definirMetaGeral(DadosMetaGeralDTO dados) {
        // Usa o método que criamos para ver se já existe uma meta para este período
        Optional<MetaGeral> metaExistenteOpt = metaGeralRepository.findByMesAndAno(dados.mes(), dados.ano());

        MetaGeral meta;
        if (metaExistenteOpt.isPresent()) {
            // Se já existe, apenas atualiza o valor
            meta = metaExistenteOpt.get();
            meta.setValor(dados.valor());
        } else {
            // Se não existe, cria uma nova
            meta = new MetaGeral();
            meta.setMes(dados.mes());
            meta.setAno(dados.ano());
            meta.setValor(dados.valor());
        }

        return metaGeralRepository.save(meta);
    }

    /**
     * Busca a meta geral de um mês/ano específico.
     * Se não encontrar, retorna uma meta com valor 0 para não quebrar os cálculos.
     */
    public MetaGeral buscarMetaDoMes(int mes, int ano) {
        return metaGeralRepository.findByMesAndAno(mes, ano)
                .orElse(new MetaGeral(null, mes, ano, BigDecimal.ZERO));
    }
}