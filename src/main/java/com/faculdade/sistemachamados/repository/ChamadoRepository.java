
package com.faculdade.sistemachamados.repository;

import com.faculdade.sistemachamados.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    List<Chamado> findByAlunoId(Long alunoId);

}
