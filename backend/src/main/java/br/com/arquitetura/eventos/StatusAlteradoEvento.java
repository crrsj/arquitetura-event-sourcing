package br.com.arquitetura.eventos;


// Evento 2: Status Alterado
public class StatusAlteradoEvento extends EventoBase {

    private String novoStatus;
    private String localizacaoAtual;

    public StatusAlteradoEvento(String idEncomenda, String novoStatus, String localizacaoAtual) {
        super(idEncomenda, "StatusAlterado");
        this.novoStatus = novoStatus;
        this.localizacaoAtual = localizacaoAtual;
    }

    public StatusAlteradoEvento() {
        super();
    }


    public String getNovoStatus() { return novoStatus; }
    public String getLocalizacaoAtual() { return localizacaoAtual; }
}
