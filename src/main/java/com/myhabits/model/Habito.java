package com.myhabits.model;

import java.time.LocalDate;

public class Habito {
    private int id;
    private int userId;
    private String nomeHabito;
    private String descricao;
    private String frequencia;
    private int prioridade;
    private LocalDate dataCriacao;
    private int streak;
    private LocalDate ultimaCompletacao;

    public Habito() {
    }

    public Habito(int id, int userId, String nomeHabito, String descricao, String frequencia, int prioridade, LocalDate dataCriacao, int streak, LocalDate ultimaCompletacao) {
        this.id = id;
        this.userId = userId;
        this.nomeHabito = nomeHabito;
        this.descricao = descricao;
        this.frequencia = frequencia;
        this.prioridade = prioridade;
        this.dataCriacao = dataCriacao;
        this.streak = streak;
        this.ultimaCompletacao = ultimaCompletacao;
    }

    public Habito(int userId, String nomeHabito, String descricao, String frequencia, int prioridade, LocalDate dataCriacao, int streak, LocalDate ultimaCompletacao) {
        this.userId = userId;
        this.nomeHabito = nomeHabito;
        this.descricao = descricao;
        this.frequencia = frequencia;
        this.prioridade = prioridade;
        this.dataCriacao = dataCriacao;
        this.streak = streak;
        this.ultimaCompletacao = ultimaCompletacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNomeHabito() {
        return nomeHabito;
    }

    public void setNomeHabito(String nomeHabito) {
        this.nomeHabito = nomeHabito;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public LocalDate getUltimaCompletacao() {
        return ultimaCompletacao;
    }

    public void setUltimaCompletacao(LocalDate ultimaCompletacao) {
        this.ultimaCompletacao = ultimaCompletacao;
    }

    @Override
    public String toString() {
        return "Habito{" +
                "id=" + id +
                ", userId=" + userId +
                ", nomeHabito='" + nomeHabito + '\'' +
                ", descricao='" + descricao + '\'' +
                ", frequencia='" + frequencia + '\'' +
                ", prioridade=" + prioridade +
                ", dataCriacao=" + dataCriacao +
                ", streak=" + streak +
                ", ultimaCompletacao=" + ultimaCompletacao +
                '}';
    }
}
