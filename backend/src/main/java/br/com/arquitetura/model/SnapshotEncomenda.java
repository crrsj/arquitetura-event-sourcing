package br.com.arquitetura.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SnapshotEncomenda {
    private String idEncomenda;
    private String statusAtual;
    private String destinatario;
    private String localizacaoAtual;
    private long ultimoNumeroEventoProcessado; // Guarda até qual evento este snapshot consolida
    private LocalDateTime dataSnapshot;

    public SnapshotEncomenda(String idEncomenda, String statusAtual, String destinatario,
                             String localizacaoAtual, long ultimoNumeroEventoProcessado) {
        this.idEncomenda = idEncomenda;
        this.statusAtual = statusAtual;
        this.destinatario = destinatario;
        this.localizacaoAtual = localizacaoAtual;
        this.ultimoNumeroEventoProcessado = ultimoNumeroEventoProcessado;
        this.dataSnapshot = LocalDateTime.now();
    }

    // Getters e Setters
    public String getIdEncomenda() {
        return idEncomenda;
    }

    public String getStatusAtual() {
        return statusAtual;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public long getUltimoNumeroEventoProcessado() {
        return ultimoNumeroEventoProcessado;
    }
}
