package br.com.seuprojeto.app_vendas.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.seuprojeto.app_vendas.entities.Cliente;
import br.com.seuprojeto.app_vendas.repositories.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + id));
    }

    public Cliente criarCliente(Cliente cliente) {
        if (clienteRepository.findByEmail(cliente.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe um cliente cadastrado com este e-mail.");
        }
        return clienteRepository.save(cliente);
    }

    public Cliente atualizarCliente(Long id, Cliente dadosAtualizados) {
        Cliente clienteExistente = this.buscarPorId(id);

        Optional<Cliente> clienteComEmail = clienteRepository.findByEmail(dadosAtualizados.getEmail());
        if (clienteComEmail.isPresent() && !clienteComEmail.get().getId().equals(id)) {
            throw new RuntimeException("O e-mail informado já está em uso por outro cliente.");
        }

        clienteExistente.setNomeContato(dadosAtualizados.getNomeContato());
        clienteExistente.setNomeEmpresa(dadosAtualizados.getNomeEmpresa());
        clienteExistente.setEmail(dadosAtualizados.getEmail());
        clienteExistente.setTelefone(dadosAtualizados.getTelefone());
        clienteExistente.setStatus(dadosAtualizados.getStatus());
        clienteExistente.setDataDeAniversario(dadosAtualizados.getDataDeAniversario());

        return clienteRepository.save(clienteExistente);
    }

    public void deletarCliente(Long id) {
        this.buscarPorId(id);
        clienteRepository.deleteById(id);
    }

    public List<Cliente> buscarAniversariantesDoDia() {
        LocalDate hoje = LocalDate.now();
        int mesAtual = hoje.getMonthValue();
        int diaAtual = hoje.getDayOfMonth();
        return clienteRepository.findAniversariantesDoDia(mesAtual, diaAtual);
    }

    // Faltava este método que o Controller precisa
    public List<Cliente> pesquisarPorTermo(String termo) {
        return clienteRepository.pesquisarPorTermo(termo);
    }
    
}