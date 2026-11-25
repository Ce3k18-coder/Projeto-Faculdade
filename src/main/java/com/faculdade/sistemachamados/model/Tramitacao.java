
package com.faculdade.sistemachamados.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tramitacoes")
public class Tramitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chamado_id", nullable = false)
    private Chamado chamado;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tecnico_id", nullable = false)
    private Usuario tecnico;

    @Column(length = 2000, nullable = false)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataTramitacao;

    public Tramitacao() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chamado getChamado() {
        return chamado;
    }

    public void setChamado(Chamado chamado) {
        this.chamado = chamado;
    }

    public Usuario getTecnico() {
        return tecnico;
    }

    public void setTecnico(Usuario tecnico) {
        this.tecnico = tecnico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataTramitacao() {
        return dataTramitacao;
    }

    public void setDataTramitacao(LocalDateTime dataTramitacao) {
        this.dataTramitacao = dataTramitacao;
    }
}
