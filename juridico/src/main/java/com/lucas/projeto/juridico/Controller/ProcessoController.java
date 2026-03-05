package com.lucas.projeto.juridico.Controller;

import com.lucas.projeto.juridico.Model.Processo;
import com.lucas.projeto.juridico.Service.ProcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processos")
@CrossOrigin(origins = "*")
@Tag(name = "Processos", description = "API para gerenciamento dos cartões de processos jurídicos")
public class ProcessoController {

    private final ProcessoService service;

    public ProcessoController(ProcessoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os processos", description = "Retorna a lista completa de processos para montar as colunas do Kanban.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Processo>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @PostMapping
    @Operation(summary = "Criar um novo processo", description = "Adiciona um novo cartão de processo no sistema. Se não for enviado status, vai para a 'Triagem'.")
    @ApiResponse(responseCode = "201", description = "Processo criado com sucesso")
    public ResponseEntity<Processo> criar(@RequestBody Processo processo) {
        Processo novoProcesso = service.criarProcesso(processo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoProcesso);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um processo", description = "Atualiza os dados de um processo existente (útil para quando a mãe mover o cartão de coluna).")
    @ApiResponse(responseCode = "200", description = "Processo atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    public ResponseEntity<Processo> atualizar(@PathVariable Long id, @RequestBody Processo processo) {
        try {
            Processo processoAtualizado = service.atualizarProcesso(id, processo);
            return ResponseEntity.ok(processoAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um processo", description = "Remove um processo do sistema permanentemente pelo seu ID.")
    @ApiResponse(responseCode = "204", description = "Processo deletado com sucesso (sem conteúdo de retorno)")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletarProcesso(id);
        return ResponseEntity.noContent().build();
    }
}