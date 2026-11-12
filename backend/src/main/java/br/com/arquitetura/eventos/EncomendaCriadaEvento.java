package br.com.arquitetura.eventos;

// Evento 1: Encomenda Criada
public class EncomendaCriadaEvento extends EventoBase{
    private String destinatario;
    private String enderecoEntrega;

    public EncomendaCriadaEvento(String idEncomenda, String destinatario, String enderecoEntrega) {
        super(idEncomenda, "EncomendaCriada");
        this.destinatario = destinatario;
        this.enderecoEntrega = enderecoEntrega;
    }
    public EncomendaCriadaEvento() {
        super();
    }

    public String getDestinatario() { return destinatario; }
    public String getEnderecoEntrega() { return enderecoEntrega; }


}
