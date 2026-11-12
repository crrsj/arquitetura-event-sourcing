package br.com.arquitetura.service;


import br.com.arquitetura.entity.VisaoEncomenda;
import br.com.arquitetura.eventos.EncomendaCriadaEvento;
import br.com.arquitetura.eventos.StatusAlteradoEvento;
import br.com.arquitetura.repository.VisaoEncomendaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class ProjecaoService {

    private final VisaoEncomendaRepository repository;

    public ProjecaoService(VisaoEncomendaRepository repository) {
        this.repository = repository;
    }

    // Atualiza a tabela do PostgreSQL com base no evento de criação
    public void projetarCriacao(EncomendaCriadaEvento evento) {
        VisaoEncomenda visao = new VisaoEncomenda(
                evento.getIdEncomenda(),
                evento.getDestinatario(),
                evento.getEnderecoEntrega(),
                "CRIADA",
                "Centro de Distribuicao Inicial"
        );
        repository.save(visao);
    }

    // Atualiza a tabela do PostgreSQL com base na alteração de status
    public void projetarAlteracaoStatus(StatusAlteradoEvento evento) {
        repository.findById(evento.getIdEncomenda()).ifPresent(visao -> {
            visao.setStatusAtual(evento.getNovoStatus());
            visao.setLocalizacaoAtual(evento.getLocalizacaoAtual());
            visao.setUltimaAtualizacao(LocalDateTime.now());
            repository.save(visao);
        });
    }
}
