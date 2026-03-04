package com.lucas.projeto.juridico.repository;

import com.lucas.projeto.juridico.model.Processo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, Long> {
}