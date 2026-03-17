package com.lucas.projeto.juridico.Controller;

import com.lucas.projeto.juridico.Model.Processo;
import com.lucas.projeto.juridico.Service.ProcessoService;
import com.lucas.projeto.juridico.Service.RelatorioExcelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processos")
@Tag(name = "Processos", description = "API para gerenciamento dos cartões de processos jurídicos")
public class ProcessoController {

    private final ProcessoService service;
    private final RelatorioExcelService excelService;

    public ProcessoController(ProcessoService service, RelatorioExcelService excelService) {
        this.service = service;
        this.excelService = excelService;
    }

    @GetMapping
    @Operation(summary = "Listar todos os processos", description = "Retorna a lista completa de processos para montar as colunas do Kanban.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<List<Processo>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/exportar")
    @Operation(summary = "Exportar processos para Excel", description = "Gera e baixa um arquivo .xlsx com todos os dados da base.")
    public ResponseEntity<byte[]> exportarParaExcel() {
        byte[] excelContent = excelService.gerarPlanilhaProcessos();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "relatorio_processos.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelContent);
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

    @PatchMapping("/{id}/arquivar")
    @Operation(summary = "Arquivar um processo", description = "Marca o processo como arquivado para que saia do Kanban principal, mas permaneça no histórico.")
    @ApiResponse(responseCode = "200", description = "Processo arquivado com sucesso")
    @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    public ResponseEntity<Processo> arquivar(@PathVariable Long id) {
        try {
            Processo processoArquivado = service.arquivarProcesso(id);
            return ResponseEntity.ok(processoArquivado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}