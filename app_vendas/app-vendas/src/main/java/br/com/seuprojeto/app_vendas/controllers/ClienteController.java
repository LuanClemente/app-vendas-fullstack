package br.com.seuprojeto.app_vendas.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

import br.com.seuprojeto.app_vendas.dto.ClienteRespostaDTO;
import br.com.seuprojeto.app_vendas.entities.Cliente;
import br.com.seuprojeto.app_vendas.entities.Usuario;
import br.com.seuprojeto.app_vendas.services.ClienteService;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    @Autowired
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ClienteRespostaDTO> criarCliente(@RequestBody Cliente cliente, Authentication authentication) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        Cliente novoCliente = clienteService.criarCliente(cliente, usuarioLogado);
        ClienteRespostaDTO respostaDTO = new ClienteRespostaDTO(
            novoCliente.getId(), 
            novoCliente.getNomeContato(), 
            novoCliente.getNomeEmpresa(), 
            novoCliente.getEmail(), 
            novoCliente.getTelefone(), 
            novoCliente.getStatus(), 
            novoCliente.getDataCadastro(),
            novoCliente.getDataDeAniversario()
        );
        return ResponseEntity.status(201).body(respostaDTO);
    }

    // READ (List All e Search)
    @GetMapping
    public List<ClienteRespostaDTO> buscarClientes(
            @RequestParam(name = "busca", required = false) String termo,
            Authentication authentication // <-- PARÂMETRO QUE FALTAVA
    ) {
        Usuario usuarioLogado = (Usuario) authentication.getPrincipal();
        List<Cliente> clientes = clienteService.listarTodos(usuarioLogado);

        if (termo != null && !termo.isBlank()) {
            clientes = clientes.stream()
                .filter(c -> c.getNomeContato().toLowerCase().contains(termo.toLowerCase()) ||
                             (c.getNomeEmpresa() != null && c.getNomeEmpresa().toLowerCase().contains(termo.toLowerCase())))
                .collect(Collectors.toList());
        }

        return clientes.stream()
                .map(c -> new ClienteRespostaDTO(
                    c.getId(), c.getNomeContato(), c.getNomeEmpresa(), 
                    c.getEmail(), c.getTelefone(), c.getStatus(), 
                    c.getDataCadastro(), c.getDataDeAniversario()))
                .collect(Collectors.toList());
    }

    // READ (Find by ID)
    @GetMapping("/{id}")
    public ResponseEntity<ClienteRespostaDTO> buscarClientePorId(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        ClienteRespostaDTO respostaDTO = new ClienteRespostaDTO(
            cliente.getId(), 
            cliente.getNomeContato(), 
            cliente.getNomeEmpresa(), 
            cliente.getEmail(), 
            cliente.getTelefone(), 
            cliente.getStatus(), 
            cliente.getDataCadastro(),
            cliente.getDataDeAniversario()
        );
        return ResponseEntity.ok(respostaDTO);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ClienteRespostaDTO> atualizarCliente(@PathVariable Long id, @RequestBody Cliente dadosCliente) {
        Cliente clienteAtualizado = clienteService.atualizarCliente(id, dadosCliente);
        ClienteRespostaDTO respostaDTO = new ClienteRespostaDTO(
            clienteAtualizado.getId(), 
            clienteAtualizado.getNomeContato(), 
            clienteAtualizado.getNomeEmpresa(), 
            clienteAtualizado.getEmail(), 
            clienteAtualizado.getTelefone(), 
            clienteAtualizado.getStatus(), 
            clienteAtualizado.getDataCadastro(),
            clienteAtualizado.getDataDeAniversario()
        );
        return ResponseEntity.ok(respostaDTO);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCliente(@PathVariable Long id) {
        clienteService.deletarCliente(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/aniversariantes")
    public ResponseEntity<List<ClienteRespostaDTO>> buscarAniversariantes() {
        List<Cliente> aniversariantes = clienteService.buscarAniversariantesDoDia();
        
        List<ClienteRespostaDTO> respostaDTOs = aniversariantes.stream()
                .map(cliente -> new ClienteRespostaDTO(
                    cliente.getId(), 
                    cliente.getNomeContato(), 
                    cliente.getNomeEmpresa(), 
                    cliente.getEmail(), 
                    cliente.getTelefone(), 
                    cliente.getStatus(), 
                    cliente.getDataCadastro(),
                    cliente.getDataDeAniversario()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(respostaDTOs);
    }
}