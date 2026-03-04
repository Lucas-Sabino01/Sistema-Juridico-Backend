package com.lucas.projeto.juridico.service;

import com.lucas.projeto.juridico.model.Processo;
import com.lucas.projeto.juridico.repository.ProcessoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcessoService {

    private final ProcessoRepository repository;

    public ProcessoService(ProcessoRepository repository) {
        this.repository = repository;
    }

    public List<Processo> listarTodos() {
        return repository.findAll();
    }

    public Processo criarProcesso(Processo processo) {
        if (processo.getStatus() == null || processo.getStatus().isEmpty()) {
            processo.setStatus("Triagem");
        }

        return repository.save(processo);
    }
}