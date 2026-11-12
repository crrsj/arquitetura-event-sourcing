package br.com.arquitetura.repository;

import br.com.arquitetura.entity.VisaoEncomenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisaoEncomendaRepository extends JpaRepository<VisaoEncomenda, String> {
}
