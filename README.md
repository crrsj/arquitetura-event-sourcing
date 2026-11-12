# 📦 Backend: Sistema de Rastreamento de Encomendas (Event Sourcing & CQRS)

Este repositório contém a implementação do backend desenvolvido em **Java 21** e **Spring Boot**, estruturado sob os padrões de arquitetura **Event Sourcing** e **CQRS** (Command Query Responsibility Segregation) para gerir o ciclo de vida logístico de encomendas.

---

## 🏗️ Arquitetura e Conceitos Aplicados

### 1. Event Sourcing (Origem de Eventos)
O estado das encomendas não é mantido por simples operações de alteração direta (`UPDATE`) em tabelas relacionais. Em vez disso, **cada mudança de negócio é registada como um evento imutável** numa sequência cronológica (*Stream*) dentro do **EventStoreDB** (via gRPC na porta `2113`).
* **Vantagens Técnicas:** Auditoria nativa por defeito, histórico cronológico imutável e capacidade total de reconstrução de estados passados.
* **Hierarquia de Eventos:** As classes de eventos (`EncomendaCriadaEvento` e `StatusAlteradoEvento`) herdam de uma classe base (`EventoBase`) e possuem construtores vazios públicos explícitos para garantir compatibilidade com a desserialização do **Jackson**.

### 2. CQRS (Command Query Responsibility Segregation)
O modelo separa claramente as operações de gravação e consulta:
* **Write Side (Comandos):** As requisições de criação (`POST`) e alteração de estado/localização (`PUT`) validam o fluxo de negócio e persistem os eventos diretamente no EventStoreDB.
* **Read Side (Consultas):** Projeções assíncronas alimentam e atualizam um modelo de leitura desnormalizado (`VisaoEncomenda`) guardado no **PostgreSQL**, permitindo consultas rápidas (`GET`) sem sobrecarregar o motor de eventos.

---

## 🛠️ Stack Tecnológica

* **Linguagem:** Java 21
* **Framework:** Spring Boot (Web, REST)
* **Event Store:** EventStoreDB (via Docker, gRPC em `127.0.0.1:2113`, TLS desativado)
* **Base de Dados Relacional (Read Model):** PostgreSQL (via Spring Data JPA / Hibernate)
* **Serialização:** Jackson (configurado para suportar hierarquia e construtores padrão)

---

## 📡 Endpoints Expostos pelo Backend

O backend expõe os seguintes recursos na porta **`8080`**:

* **`POST /api/encomendas`** — Regista uma nova encomenda e emite o evento inicial (`EncomendaCriada`).
* **`PUT /api/encomendas/{id}/status`** — Atualiza o estado logístico e a localização atual, emitindo o evento subsequente (`StatusAlterado`).
* **`GET /api/encomendas/{id}`** — Consulta a visão geral consolidada na base de dados de leitura do PostgreSQL (CQRS).
* **`GET /api/encomendas/{id}/linha-do-tempo`** — Realiza a leitura crua (*Raw Read*) do *stream* imutável diretamente do EventStoreDB (Event Sourcing).

---

<img width="1920" height="1080" alt="EV1" src="https://github.com/user-attachments/assets/a7cc84b6-1de5-46c0-b72c-c5537677b280" />
<img width="1920" height="1080" alt="EV4" src="https://github.com/user-attachments/assets/fc967315-c559-4ef0-853c-6e85978b50bd" />
<img width="1920" height="1080" alt="EV3" src="https://github.com/user-attachments/assets/0f896fcc-280b-4548-9732-e56698381c83" />
<img width="1920" height="1080" alt="EV2" src="https://github.com/user-attachments/assets/b30cfcec-3708-44a9-b10d-87c96da39bc8" />

