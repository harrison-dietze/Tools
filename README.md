# Projeto Spring Boot - Tools

Este é um projeto Spring Boot que oferece uma API REST para gerenciar pagamentos, disponibilizando as funcionalidades de realizar pagamentos e estornos. O sistema trabalha com banco de dados relacional em memória (H2 database) para facilitar a configuração do ambiente.

## Pré-requisitos:

- JDK 21 ou superior
- Maven

## Como rodar o projeto:

1. Limpe as dependências:
    ```bash
    mvn clean
    ```

2. Realize o build:
    ```bash
    mvn build
    ```

3. Rode o projeto:
    ```bash
    mvn spring-boot:run
    ```

## Funcionalidades:

O sistema possui uma API Rest para cadastro de pagamentos (`/payments`) e estornos (`/reversals`).
