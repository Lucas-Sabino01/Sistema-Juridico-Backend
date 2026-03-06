package com.lucas.projeto.juridico.Model;

import com.lucas.projeto.juridico.Security.CryptoConverter;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "processo_etiquetas", joinColumns = @JoinColumn(name = "processo_id"))
    @Column(name = "etiqueta")
    private List<String> etiquetas = new ArrayList<>();

    private String honorarios;

    @Convert(converter = CryptoConverter.class)
    private String cpfCliente;

    @Convert(converter = CryptoConverter.class)
    private String senhaGov;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean arquivado = false;
}