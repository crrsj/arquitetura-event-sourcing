package br.com.arquitetura.controller;


import br.com.arquitetura.entity.VisaoEncomenda;
import br.com.arquitetura.eventos.EncomendaCriadaEvento;
import br.com.arquitetura.eventos.StatusAlteradoEvento;
import br.com.arquitetura.repository.VisaoEncomendaRepository;
import br.com.arquitetura.service.EventStoreService;
import br.com.arquitetura.service.ProjecaoService;
import com.eventstore.dbclient.EventStoreDBClient;
import com.eventstore.dbclient.ReadResult;
import com.eventstore.dbclient.ReadStreamOptions;
import com.eventstore.dbclient.ResolvedEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/encomendas")
@CrossOrigin(origins = "*")
@Tag(name = "Encomendas", description = "Endpoints de gerenciamento, escrita de eventos e consulta de rastreamento de encomendas")
public class EncomendaController {

    private final ObjectMapper objectMapper;
    private final EventStoreService eventStoreService;
    private final ProjecaoService projecaoService;
    private final VisaoEncomendaRepository visaoRepository;
    private final EventStoreDBClient eventStoreClient;


    public EncomendaController(ObjectMapper objectMapper, EventStoreService eventStoreService, ProjecaoService projecaoService, VisaoEncomendaRepository visaoRepository, EventStoreDBClient eventStoreClient) {
        this.objectMapper = objectMapper;
        this.eventStoreService = eventStoreService;
        this.projecaoService = projecaoService;
        this.visaoRepository = visaoRepository;
        this.eventStoreClient = eventStoreClient;

    }


    // 1. COMANDO: Criar Encomenda (Escrita no EventStoreDB + Projeção no PostgreSQL)


    @Operation(summary = "Criar nova encomenda", description = "Gera o evento inicial de criação da encomenda no EventStoreDB e atualiza a projeção no PostgreSQL.")
    @PostMapping
    public String criarEncomenda(@RequestBody Map<String, String> payload) throws Exception {
        String id = payload.get("idEncomenda");
        String destinatario = payload.get("destinatario");
        String endereco = payload.get("enderecoEntrega");

        EncomendaCriadaEvento evento = new EncomendaCriadaEvento(id, destinatario, endereco);

        // Grava o evento imutável no banco de escrita (EventStoreDB)
        eventStoreService.salvarEvento(id, evento);

        // Atualiza a projeção no banco de leitura (PostgreSQL)
        projecaoService.projetarCriacao(evento);

        return "Encomenda " + id + " registrada com sucesso!";
    }

    // 2. COMANDO: Atualizar Status da Encomenda
    @Operation(summary = "Alterar status da encomenda", description = "Adiciona um evento de mudança de status/localização na stream do EventStoreDB e atualiza a base de leitura.")
    @PutMapping("/{id}/status")
    public String alterarStatus(@PathVariable String id, @RequestBody Map<String, String> payload) throws Exception {
        String novoStatus = payload.get("novoStatus");
        String localizacao = payload.get("localizacaoAtual");

        StatusAlteradoEvento evento = new StatusAlteradoEvento(id, novoStatus, localizacao);

        // Grava no EventStoreDB
        eventStoreService.salvarEvento(id, evento);

        // Atualiza a projeção no PostgreSQL
        projecaoService.projetarAlteracaoStatus(evento);

        return "Status da encomenda " + id + " atualizado para: " + novoStatus;
    }

    // 3. CONSULTA (CQRS): Obtém o estado consolidado lendo do PostgreSQL


    @Operation(summary = "Consultar pelo id", description = "Busca o estado atual da encomenda diretamente na tabela de leitura do PostgreSQL (lado CQRS de Leitura).")
    @GetMapping("/{id}")
    public VisaoEncomenda obterVisaoGeral(
            @Parameter(description = "ID único da encomenda", example = "enc-123") @PathVariable String id) {
        return visaoRepository.findById(id).orElse(null);
    }

    @Operation(
            summary = "Consultar linha do tempo (Event Sourcing)",
            description = "Lê o histórico completo de eventos diretamente do EventStoreDB."
    )
    @GetMapping("/{id}/linha-do-tempo")
    public List<Object> obterLinhaDoTempo(@PathVariable String id) throws Exception {

        String nomeStream = "encomenda-" + id;

        ReadResult result = eventStoreClient
                .readStream(nomeStream, ReadStreamOptions.get())
                .get();

        List<Object> eventos = new ArrayList<>();

        for (ResolvedEvent re : result.getEvents()) {

            String tipo = re.getEvent().getEventType();
            byte[] data = re.getEvent().getEventData();

            // Converte o JSON armazenado em uma árvore de objetos
            JsonNode node = objectMapper.readTree(data);

            // Verifica o formato antigo da dataHora
            JsonNode dataHora = node.get("dataHora");

            if (dataHora != null && dataHora.isArray() && dataHora.size() == 3) {

                String dataCorrigida = String.format(
                        "%04d-%02d-%02dT00:00:00",
                        dataHora.get(0).asInt(),
                        dataHora.get(1).asInt(),
                        dataHora.get(2).asInt()
                );

                ((ObjectNode) node).put("dataHora", dataCorrigida);
            }

            // Desserializa conforme o tipo do evento
            if (tipo.equals("EncomendaCriada")) {

                eventos.add(
                        objectMapper.treeToValue(node, EncomendaCriadaEvento.class)
                );

            } else if (tipo.equals("StatusAlterado")) {

                eventos.add(
                        objectMapper.treeToValue(node, StatusAlteradoEvento.class)
                );
            }
        }

        return eventos;
    }


}

