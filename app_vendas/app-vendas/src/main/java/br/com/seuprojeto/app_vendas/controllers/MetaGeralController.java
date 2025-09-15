package br.com.seuprojeto.app_vendas.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.seuprojeto.app_vendas.dto.DadosMetaGeralDTO;
import br.com.seuprojeto.app_vendas.dto.MetaGeralRespostaDTO;
import br.com.seuprojeto.app_vendas.entities.MetaGeral;
import br.com.seuprojeto.app_vendas.services.MetaGeralService;

@RestController
@RequestMapping("/api/metas-gerais")
public class MetaGeralController {

    private final MetaGeralService metaGeralService;

    public MetaGeralController(MetaGeralService metaGeralService) {
        this.metaGeralService = metaGeralService;
    }

    @PostMapping
    public ResponseEntity<MetaGeralRespostaDTO> definirMetaGeral(@RequestBody DadosMetaGeralDTO dados) {
        MetaGeral metaSalva = metaGeralService.definirMetaGeral(dados);
        
        MetaGeralRespostaDTO resposta = new MetaGeralRespostaDTO(
            metaSalva.getId(),
            metaSalva.getMes(),
            metaSalva.getAno(),
            metaSalva.getValor()
        );

        return ResponseEntity.ok(resposta);
    }
}