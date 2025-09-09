package br.com.seuprojeto.app_vendas.dto;

import java.time.LocalDate;
import java.time.LocalDateTime; // Adicione esta importação

// A ordem dos campos aqui deve ser a mesma usada no Controller
public record ClienteRespostaDTO(
    Long id, 
    String nomeContato, 
    String nomeEmpresa, 
    String email, 
    String telefone, 
    String status, 
    LocalDateTime dataCadastro,
    LocalDate dataDeAniversario
) {
}