# Almoxarifado V3

Sistema de controle de estoque e movimentações de almoxarifado desenvolvido com Java e Spring Boot.

O projeto tem como objetivo simular regras de negócio de um sistema de almoxarifado, controlando produtos, filiais, entradas, saídas e movimentações de estoque, com foco em boas práticas de desenvolvimento backend e aprendizado do ecossistema Spring.

## 🎯 Objetivo

O sistema deve controlar o estoque dos produtos em cada filial, suas entradas e saídas e o histórico das movimentações realizadas.

Este projeto é a terceira versão de um sistema desenvolvido anteriormente utilizando Java e JDBC. Nesta versão, o objetivo é reconstruir a aplicação utilizando o ecossistema Spring, permitindo comparar a persistência manual com uma abordagem baseada em JPA/Hibernate e Spring Data JPA.

## 🛠️ Tecnologias

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate
- Spring Web
- Spring Validation
- MySQL
- Maven
- Git

## 📦 Domínio

O sistema possui os seguintes conceitos principais:

- **Product** — produto cadastrado no sistema.
- **Branch** — filial da empresa.
- **BranchProduct** — relação entre um produto e uma filial, contendo informações específicas daquela filial, como quantidade em estoque e localização.
- **Invoice** — nota fiscal utilizada para registrar entradas de materiais.
- **ProductInvoice** — item de uma nota fiscal, contendo produto, quantidade e destino.
- **Request** — requisição de materiais realizada por uma filial.
- **ProductRequest** — item de uma requisição, contendo quantidade solicitada e quantidade atendida.
- **Movement** — histórico das movimentações realizadas no estoque.

### Destino do item da Nota Fiscal

Os itens de uma nota fiscal podem possuir diferentes destinos:

- `STOCK` — o item movimenta o estoque e gera uma entrada.
- `DIRECT` — o item é apenas registrado e não movimenta o estoque.

### Tipos de movimentação

- `ENTRY` — entrada de estoque.
- `OUTPUT` — saída de estoque.
- `REVERSAL` — estorno de uma movimentação anterior.

## 📋 Principais regras de negócio

Entre as regras previstas para o sistema estão:

- Um produto possui um código único.
- Uma filial possui um código único.
- Um produto pode existir em várias filiais.
- Um produto pode possuir quantidade e localização diferentes em cada filial.
- A relação entre produto e filial é representada por `BranchProduct`.
- Uma filial pode receber notas fiscais.
- Uma nota fiscal pertence a uma única filial.
- Uma nota fiscal pode possuir vários produtos.
- O número da nota fiscal é único em conjunto com o fornecedor.
- Uma requisição pertence a uma única filial.
- Uma requisição pode possuir vários produtos.
- A quantidade atendida não pode ser superior à quantidade solicitada.
- As movimentações de estoque são registradas para manter o histórico das operações.
- Estornos não devem apagar a movimentação original, mas registrar uma nova movimentação de reversão.

## 🏗️ Arquitetura

A aplicação será construída seguindo uma separação de responsabilidades entre as principais camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
JPA / Hibernate
    ↓
MySQL
```
