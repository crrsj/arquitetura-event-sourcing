package br.com.arquitetura.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "visao_encomendas")
public class VisaoEncomenda {
    @Id
    private String idEncomenda;
    private String destinatario;
    private String enderecoEntrega;
    private String statusAtual;
    private String localizacaoAtual;
    private LocalDateTime ultimaAtualizacao;

    public VisaoEncomenda() {}

    public VisaoEncomenda(String idEncomenda, String destinatario, String enderecoEntrega, String statusAtual, String localizacaoAtual) {
        this.idEncomenda = idEncomenda;
        this.destinatario = destinatario;
        this.enderecoEntrega = enderecoEntrega;
        this.statusAtual = statusAtual;
        this.localizacaoAtual = localizacaoAtual;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public String getIdEncomenda() { return idEncomenda; }
    public String getDestinatario() { return destinatario; }
    public String getEnderecoEntrega() { return enderecoEntrega; }
    public String getStatusAtual() { return statusAtual; }
    public void setStatusAtual(String statusAtual) { this.statusAtual = statusAtual; }
    public String getLocalizacaoAtual() { return localizacaoAtual; }
    public void setLocalizacaoAtual(String localizacaoAtual) { this.localizacaoAtual = localizacaoAtual; }
    public LocalDateTime getUltimaAtualizacao() { return ultimaAtualizacao; }
    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) { this.ultimaAtualizacao = ultimaAtualizacao; }
}
