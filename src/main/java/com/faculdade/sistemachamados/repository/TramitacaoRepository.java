
package com.faculdade.sistemachamados.repository;

import com.faculdade.sistemachamados.model.Tramitacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TramitacaoRepository extends JpaRepository<Tramitacao, Long> {

    List<Tramitacao> findByChamadoIdOrderByDataTramitacaoAsc(Long chamadoId);

}
