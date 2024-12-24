package com.devsuperior.desafio.orm.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_bloco")
public class Bloco {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
  private Instant inicio;

  @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
  private Instant fim;

  @ManyToOne
  @JoinColumn(name = "atividade_id")
  private Atividade atividade;

  public Bloco() {
  }

  public Bloco(Integer id, Instant inicio, Instant fim, Atividade atividade) {
    this.id = id;
    this.inicio = inicio;
    this.fim = fim;
    this.atividade = atividade;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Instant getInicio() {
    return inicio;
  }

  public void setInicio(Instant inicio) {
    this.inicio = inicio;
  }

  public Instant getFim() {
    return fim;
  }

  public void setFim(Instant fim) {
    this.fim = fim;
  }

  public Atividade getAtividade() {
    return atividade;
  }

  public void setAtividade(Atividade atividade) {
    this.atividade = atividade;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Bloco other = (Bloco) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

}
