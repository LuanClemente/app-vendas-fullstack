package br.com.seuprojeto.app_vendas.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.seuprojeto.app_vendas.dto.DadosMetaDTO;
import br.com.seuprojeto.app_vendas.dto.MetaRespostaDTO;
import br.com.seuprojeto.app_vendas.entities.Meta;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.services.MetaService;

@RestController
@RequestMapping("/api/metas")
public class MetaController {

    private final MetaService metaService;

    // @Autowired removido daqui
    public MetaController(MetaService metaService) {
        this.metaService = metaService;
    }

    @PostMapping
    public ResponseEntity<MetaRespostaDTO> definirMeta(@RequestBody DadosMetaDTO dados) {
        // 1. Chama o serviço que tem a lógica de criar ou atualizar a meta.
        Meta metaSalva = metaService.definirMeta(dados);

        // 2. Converte a entidade para o nosso DTO de resposta.
        MetaRespostaDTO respostaDTO = new MetaRespostaDTO(
            metaSalva.getId(),
            metaSalva.getValor(),
            metaSalva.getMes(),
            metaSalva.getAno(),
            metaSalva.getUsuario().getId()
        );

        // 3. Retorna 200 OK com os dados da meta definida.
        return ResponseEntity.ok(respostaDTO);
    }
 @GetMapping
    public ResponseEntity<List<MetaRespostaDTO>> listarMetas(Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        List<Meta> metas = metaService.listarMetasPorUsuario(usuarioLogado);

        List<MetaRespostaDTO> respostaDTOs = metas.stream().map(meta -> 
            new MetaRespostaDTO(
                meta.getId(),
                meta.getValor(),
                meta.getMes(),
                meta.getAno(),
                meta.getUsuario().getId()
            )
        ).collect(Collectors.toList());

        return ResponseEntity.ok(respostaDTOs);
    }
}