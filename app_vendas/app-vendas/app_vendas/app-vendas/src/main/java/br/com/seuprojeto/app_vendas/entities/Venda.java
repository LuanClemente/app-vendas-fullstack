package br.com.seuprojeto.app_vendas.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valor;

    private LocalDate dataDaVenda;

    private String descricao;

    private boolean faturada = false;

    // --- RELACIONAMENTOS ---
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario; // Para esta linha funcionar, a importação do Usuario é necessária

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente; // Para esta linha funcionar, a importação do Cliente é necessária
}