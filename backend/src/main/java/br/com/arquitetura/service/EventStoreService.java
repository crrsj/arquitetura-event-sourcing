package br.com.arquitetura.service;

import br.com.arquitetura.eventos.EventoBase;
import br.com.arquitetura.model.SnapshotEncomenda;
import com.eventstore.dbclient.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventStoreService {

    private final EventStoreDBClient client;
    private final ObjectMapper objectMapper;

    // Regra: Criar Snapshot a cada 3 eventos gravados na stream
    private static final int INTERVALO_SNAPSHOT = 3;

    public EventStoreService(EventStoreDBClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public void salvarEvento(String idEncomenda, EventoBase evento) throws Exception {
        // Nome da Stream conforme explicado no vídeo: "entidade-id"
        String nomeStream = "encomenda-" + idEncomenda;

        // Utiliza o ObjectMapper injetado pelo Spring
        byte[] dadosJson = objectMapper.writeValueAsBytes(evento);

        EventData eventData = EventDataBuilder.json(
                UUID.randomUUID(),
                evento.getTipoEvento(),
                dadosJson
        ).build();

        // Salva o evento no EventStoreDB
        WriteResult resultado = client.appendToStream(nomeStream, eventData).get();

        // Checa se precisa gerar um Snapshot
        checarEGerarSnapshot(nomeStream, idEncomenda, resultado.getNextExpectedRevision().toRawLong());
    }

    private void checarEGerarSnapshot(String nomeStream, String idEncomenda, long totalEventos) throws Exception {
        if (totalEventos > 0 && totalEventos % INTERVALO_SNAPSHOT == 0) {
            // Recorre a stream para consolidar o estado atual
            ReadResult readResult = client.readStream(nomeStream, ReadStreamOptions.get()).get();

            String status = "Desconhecido";
            String destinatario = "";
            String localizacao = "Origem";

            for (ResolvedEvent re : readResult.getEvents()) {
                String tipo = re.getEvent().getEventType();
                byte[] data = re.getEvent().getEventData();

                if (tipo.equals("EncomendaCriada")) {
                    var ev = objectMapper.readValue(data, br.com.arquitetura.eventos.EncomendaCriadaEvento.class);
                    destinatario = ev.getDestinatario();
                    status = "CRIADA";
                } else if (tipo.equals("StatusAlterado")) {
                    var ev = objectMapper.readValue(data, br.com.arquitetura.eventos.StatusAlteradoEvento.class);
                    status = ev.getNovoStatus();
                    localizacao = ev.getLocalizacaoAtual();
                }
            }

            SnapshotEncomenda snapshot = new SnapshotEncomenda(idEncomenda, status, destinatario, localizacao, totalEventos);

            // Grava o Snapshot em uma stream separada de snapshots: "encomenda-snapshot-id"
            String nomeStreamSnapshot = "encomenda-snapshot-" + idEncomenda;
            EventData snapshotData = EventDataBuilder.json(
                    UUID.randomUUID(),
                    "SnapshotGerado",
                    objectMapper.writeValueAsBytes(snapshot)
            ).build();

            client.appendToStream(nomeStreamSnapshot, snapshotData).get();
            System.out.println("📸 [SNAPSHOT CRIADO] Fotografia salva para " + nomeStream + " no evento #" + totalEventos);
        }
    }
}
