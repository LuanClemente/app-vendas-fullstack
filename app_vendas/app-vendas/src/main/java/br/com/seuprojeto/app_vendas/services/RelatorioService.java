package br.com.seuprojeto.app_vendas.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.seuprojeto.app_vendas.dto.DashboardSupervisorDTO;
import br.com.seuprojeto.app_vendas.dto.DashboardVendedorDTO;
import br.com.seuprojeto.app_vendas.dto.DesempenhoVendedorDTO;
import br.com.seuprojeto.app_vendas.dto.ProgressoMetaClienteDTO;
import br.com.seuprojeto.app_vendas.dto.TopClienteDTO;
import br.com.seuprojeto.app_vendas.entities.Meta;
import br.com.seuprojeto.app_vendas.entities.MetaCliente;
import br.com.seuprojeto.app_vendas.entities.MetaGeral;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.entities.Venda;
import br.com.seuprojeto.app_vendas.repositories.MetaRepository;
import br.com.seuprojeto.app_vendas.repositories.UsuarioRepository;
import br.com.seuprojeto.app_vendas.repositories.VendaRepository;

@Service
public class RelatorioService {
    
    private final VendaRepository vendaRepository;
    private final MetaRepository metaRepository;
    private final MetaClienteService metaClienteService;
    private final UsuarioRepository usuarioRepository;
    private final MetaGeralService metaGeralService;

    @Autowired
    public RelatorioService(
            VendaRepository vendaRepository, 
            MetaRepository metaRepository, 
            MetaClienteService metaClienteService, 
            UsuarioRepository usuarioRepository,
            MetaGeralService metaGeralService
    ) {
        this.vendaRepository = vendaRepository;
        this.metaRepository = metaRepository;
        this.metaClienteService = metaClienteService;
        this.usuarioRepository = usuarioRepository;
        this.metaGeralService = metaGeralService;
    }

    public List<TopClienteDTO> buscarTopClientesDoMes(Usuario usuarioLogado) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioDoMes = hoje.withDayOfMonth(1);
        LocalDate fimDoMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
        return vendaRepository.findTop10ClientesByPeriodo(usuarioLogado.getId(), inicioDoMes, fimDoMes);
    }
    
    public BigDecimal calcularTotalVendidoClienteNoMes(Long idCliente) {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioDoMes = hoje.withDayOfMonth(1);
        LocalDate fimDoMes = hoje.withDayOfMonth(hoje.lengthOfMonth());
        List<Venda> vendasDoMes = vendaRepository.findVendasByClienteAndPeriodo(idCliente, inicioDoMes, fimDoMes);
        return vendasDoMes.stream()
                         .map(Venda::getValor)
                         .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // =======================================================
    //  MÉTODO QUE FALTAVA ADICIONADO AQUI
    // =======================================================
    public BigDecimal calcularMediaComprasCliente(Long idCliente) {
        List<Venda> todasAsVendas = vendaRepository.findByClienteId(idCliente);
        if (todasAsVendas.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = todasAsVendas.stream()
                                     .map(Venda::getValor)
                                     .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(new BigDecimal(todasAsVendas.size()), 2, RoundingMode.HALF_UP);
    }

    public DashboardVendedorDTO getDashboardVendedor(Usuario vendedor) {
        LocalDate hoje = LocalDate.now();
        int mesAtual = hoje.getMonthValue();
        int anoAtual = hoje.getYear();
        LocalDate inicioDoMes = hoje.withDayOfMonth(1);
        LocalDate fimDoMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        Meta metaDoMes = metaRepository
            .findByUsuarioIdAndMesAndAno(vendedor.getId(), mesAtual, anoAtual)
            .orElse(new Meta(null, BigDecimal.ZERO, mesAtual, anoAtual, vendedor));

        List<Venda> vendasDoMes = vendaRepository.findVendasByUsuarioAndPeriodo(vendedor.getId(), inicioDoMes, fimDoMes);
        BigDecimal totalVendido = vendasDoMes.stream().map(Venda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCortes = vendasDoMes.stream().filter(venda -> venda.getValor().compareTo(BigDecimal.ZERO) < 0).map(Venda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFaturado = vendasDoMes.stream().filter(Venda::isFaturada).map(Venda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        // Novo cálculo: abate cortes apenas do total faturado
        BigDecimal totalFaturadoComCortes = totalFaturado.add(totalCortes);
        BigDecimal valorMeta = metaDoMes.getValor();
        BigDecimal valorFaltante = valorMeta.subtract(totalVendido).max(BigDecimal.ZERO);
        double porcentagemAtingida = 0.0;
        if (valorMeta.compareTo(BigDecimal.ZERO) > 0) {
            porcentagemAtingida = totalVendido.divide(valorMeta, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
        }
        double porcentagemFaturada = 0.0;
        if (valorMeta.compareTo(BigDecimal.ZERO) > 0) {
            porcentagemFaturada = totalFaturadoComCortes.divide(valorMeta, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
        }
        return new DashboardVendedorDTO(valorMeta, totalVendido, valorFaltante, porcentagemAtingida, totalCortes.abs(), totalFaturadoComCortes, porcentagemFaturada);
    }

    public List<ProgressoMetaClienteDTO> getProgressoMetasClientes(Usuario usuario, int mes, int ano) {
        List<MetaCliente> metas = metaClienteService.listarMetasPorVendedor(usuario.getId(), mes, ano);
        return metas.stream().map(meta -> {
            BigDecimal totalVendido = calcularTotalVendidoClienteNoMes(meta.getCliente().getId());
            BigDecimal valorMeta = meta.getValor();
            double porcentagem = 0.0;
            if (valorMeta.compareTo(BigDecimal.ZERO) > 0) {
                porcentagem = totalVendido.divide(valorMeta, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
            }
            return new ProgressoMetaClienteDTO(meta.getId(), meta.getCliente().getNomeContato(), meta.getCliente().getNomeEmpresa(), valorMeta, totalVendido, porcentagem);
        }).collect(Collectors.toList());
    }

    public DashboardSupervisorDTO getDashboardSupervisor() {
        LocalDate hoje = LocalDate.now();
        int mesAtual = hoje.getMonthValue();
        int anoAtual = hoje.getYear();
        LocalDate inicioDoMes = hoje.withDayOfMonth(1);
        LocalDate fimDoMes = hoje.withDayOfMonth(hoje.lengthOfMonth());

        MetaGeral metaGeral = metaGeralService.buscarMetaDoMes(mesAtual, anoAtual);
        BigDecimal valorMetaGeral = metaGeral.getValor();

        List<Venda> todasAsVendasDoMes = vendaRepository.findAllByDataDaVendaBetween(inicioDoMes, fimDoMes);

        BigDecimal totalVendidoGeral = todasAsVendasDoMes.stream().map(Venda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFaturadoGeral = todasAsVendasDoMes.stream().filter(Venda::isFaturada).map(Venda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal faltaParaMetaGeral = valorMetaGeral.subtract(totalVendidoGeral).max(BigDecimal.ZERO);
        BigDecimal totalCortesGeral = todasAsVendasDoMes.stream().filter(v -> v.getValor().compareTo(BigDecimal.ZERO) < 0).map(Venda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalVendidoPositivo = todasAsVendasDoMes.stream().filter(v -> v.getValor().compareTo(BigDecimal.ZERO) > 0).map(Venda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFaturadoPositivo = todasAsVendasDoMes.stream().filter(v -> v.isFaturada() && v.getValor().compareTo(BigDecimal.ZERO) > 0).map(Venda::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal faltaParaFaturarGeral = totalVendidoPositivo.subtract(totalFaturadoPositivo);

        double porcentagemGeralAtingida = 0.0;
        if (valorMetaGeral.compareTo(BigDecimal.ZERO) > 0) {
            porcentagemGeralAtingida = totalVendidoGeral.divide(valorMetaGeral, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
        }

        double porcentagemGeralFaturada = 0.0;
        if (valorMetaGeral.compareTo(BigDecimal.ZERO) > 0) {
            porcentagemGeralFaturada = totalFaturadoGeral.divide(valorMetaGeral, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
        }

        return new DashboardSupervisorDTO(
            valorMetaGeral, totalVendidoGeral, totalFaturadoGeral, 
            totalCortesGeral.abs(), faltaParaFaturarGeral, faltaParaMetaGeral, 
            porcentagemGeralAtingida, porcentagemGeralFaturada);
    }

    public List<DesempenhoVendedorDTO> getDesempenhoDaEquipe() {
        List<Usuario> todosOsUsuarios = usuarioRepository.findAll();
        return todosOsUsuarios.stream().map(usuario -> {
            DashboardVendedorDTO dadosVendedor = getDashboardVendedor(usuario);
            return new DesempenhoVendedorDTO(
                usuario.getNome(),
                dadosVendedor.valorMeta(),
                dadosVendedor.totalVendido(),
                dadosVendedor.porcentagemAtingida()
            );
        }).collect(Collectors.toList());
    }
}