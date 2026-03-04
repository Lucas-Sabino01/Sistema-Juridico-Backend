package com.lucas.projeto.juridico.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String numeroProcesso;

    @Column(nullable = false)
    private String nomeCliente;

    private String status;

    private String descricao;

    private LocalDate dataPrazo;
}