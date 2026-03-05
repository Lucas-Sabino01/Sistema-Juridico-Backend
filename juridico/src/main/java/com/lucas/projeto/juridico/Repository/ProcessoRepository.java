package com.lucas.projeto.juridico.Repository;

import com.lucas.projeto.juridico.Model.Processo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, Long> {
}