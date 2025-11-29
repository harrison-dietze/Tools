# Projeto ToolsChallenge: API REST de Pagamentos

Este projeto é uma **API REST** desenvolvida em **Java** com **Spring Boot 3** para simular o gerenciamento de transações de cartão de crédito (pagamento, estorno e consulta). O foco primário foi entregar um código de **alta qualidade, manutenível e reutilizável**, utilizando padrões de engenharia de software modernos, conforme os requisitos do desafio.

---

## Design e Arquitetura

A solução é construída sob os princípios do **Domain-Driven Design (DDD)** e da **Arquitetura Limpa (Clean Architecture)**.

* **Camada de Domínio:** Contém as entidades de negócio (`Pagamento`, `FormaPagamento`) e os contratos de persistência (`PagamentoRepository`).
* **Camada de Aplicação (Use Cases):** Contém a lógica de negócio e as regras de autorização (`ProcessarPagamento`, `EstornarPagamento`). Esta camada é agnóstica à interface e à infraestrutura.
* **Camada de Infraestrutura:** Implementa os contratos de repositório (JPA/H2) e os mecanismos de suporte, como o sistema de **Idempotência**.
* **Tecnologias:** Java, Spring Boot, Spring Data JPA, H2 Database (em memória).

## Regras de Negócio e Engenharia

O sistema implementa validações e mecanismos cruciais para um sistema financeiro:

1.  **Idempotência em POST:** O *endpoint* de pagamento (`POST /api/pagamentos`) é **idempotente**. O cliente deve fornecer uma `Idempotency-Key` no *header*. Requisições repetidas com a mesma chave retornam o resultado da primeira tentativa, prevenindo cobranças duplicadas.
2.  **Unicidade:** O sistema garante que o ID do pagamento é único. O ID pode ser fornecido ou gerado (UUID) para rastreamento.
3.  **Controle de Status:** O sistema gerencia o *status* da transação, permitindo apenas estorno (mudança para `CANCELADO`) em pagamentos que foram `AUTORIZADO`.
4.  **Validação de Entrada:** Validações de formato e consistência (ex: formato de data e consistência de parcelas/tipo de pagamento) são aplicadas na entrada da API.

---

## Instruções de Inicialização

### Pré-requisitos

* **JDK 21+**
* **Apache Maven**

### Execução

1.  **Build:** Compile e empacote o projeto:
    ```bash
    mvn clean install
    ```
2.  **Run:** Inicie a aplicação Spring Boot (o H2 será inicializado automaticamente):
    ```bash
    mvn spring-boot:run
    ```
    A API estará operacional em `http://localhost:8080`.

---

## Endpoints da API REST

A URL base para todas as operações é **`/api/pagamentos`**.

| Operação | Método | URL | Headers Chave | Status de Sucesso |
| :--- | :--- | :--- | :--- | :--- |
| **Pagamento** | `POST` | `/api/pagamentos` | `Idempotency-Key` | `201 CREATED` |
| **Estorno** | `POST` | `/api/pagamentos/{id}/estorno` | | `200 OK` |
| **Detalhar** | `GET` | `/api/pagamentos/{id}` | | `200 OK` |
| **Listar** | `GET` | `/api/pagamentos` | | `200 OK` |

### Teste de Idempotência

Para validar a idempotência, envie o mesmo `POST /api/pagamentos` duas vezes, usando exatamente o mesmo valor no *header* `Idempotency-Key`. A segunda requisição deve retornar a resposta da primeira tentativa (sem criar um segundo registro no banco).