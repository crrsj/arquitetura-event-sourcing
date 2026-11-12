package br.com.arquitetura.eventos;


import java.time.LocalDateTime;


public abstract class EventoBase {

    private String idEncomenda;
    private String tipoEvento;
    private LocalDateTime dataHora;

    public EventoBase(String idEncomenda, String tipoEvento) {
        this.idEncomenda = idEncomenda;
        this.tipoEvento = tipoEvento;
        this.dataHora = LocalDateTime.now();
    }

    public EventoBase() {
    }

    public String getIdEncomenda() { return idEncomenda; }
    public String getTipoEvento() { return tipoEvento; }
    public LocalDateTime getDataHora() { return dataHora; }

    public void setIdEncomenda(String idEncomenda) {
        this.idEncomenda = idEncomenda;
    }

    public void setTipoEvento(String tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}


