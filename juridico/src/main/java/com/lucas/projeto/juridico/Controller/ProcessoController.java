package com.lucas.projeto.juridico.controller;

import com.lucas.projeto.juridico.model.Processo;
import com.lucas.projeto.juridico.service.ProcessoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processos")
@CrossOrigin(origins = "*")
public class ProcessoController {

    private final ProcessoService service;

    public ProcessoController(ProcessoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Processo>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    public ResponseEntity<Processo> criar(@RequestBody Processo processo) {
        Processo novoProcesso = service.criarProcesso(processo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProcesso);
    }
}