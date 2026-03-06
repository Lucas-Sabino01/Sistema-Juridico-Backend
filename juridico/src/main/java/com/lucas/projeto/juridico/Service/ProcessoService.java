package com.lucas.projeto.juridico.Service;

import com.lucas.projeto.juridico.Model.Processo;
import com.lucas.projeto.juridico.Repository.ProcessoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        processo.setId(null);

        if (processo.getStatus() == null || processo.getStatus().isEmpty()) {
            processo.setStatus("Triagem");
        }

        return repository.save(processo);
    }
    public Processo atualizarProcesso(Long id, Processo processoAtualizado) {
        Optional<Processo> processoOptional = repository.findById(id);
        if (processoOptional.isPresent()) {
            Processo processoExistente = processoOptional.get();
            processoExistente.setNumeroProcesso(processoAtualizado.getNumeroProcesso());
            processoExistente.setNomeCliente(processoAtualizado.getNomeCliente());
            processoExistente.setStatus(processoAtualizado.getStatus());
            processoExistente.setDescricao(processoAtualizado.getDescricao());
            processoExistente.setDataPrazo(processoAtualizado.getDataPrazo());
            processoExistente.setEtiquetas(processoAtualizado.getEtiquetas());
            processoExistente.setHonorarios(processoAtualizado.getHonorarios());
            processoExistente.setCpfCliente(processoAtualizado.getCpfCliente());
            processoExistente.setSenhaGov(processoAtualizado.getSenhaGov());
            processoExistente.setArquivado(processoAtualizado.isArquivado());
            return repository.save(processoExistente);
        } else {
            throw new RuntimeException("Processo não encontrado com o ID: " + id);
        }
    }

    public void deletarProcesso(Long id) {
        repository.deleteById(id);
    }

    public Processo arquivarProcesso(Long id) {
        return repository.findById(id).map(processo -> {
            processo.setArquivado(true);
            // Regra de negócio: Quando arquivamos, ele automaticamente é considerado "Concluído"
            processo.setStatus("Concluído");
            return repository.save(processo);
        }).orElseThrow(() -> new RuntimeException("Processo não encontrado com o ID: " + id));
    }
}