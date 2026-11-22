# Almoxarifado V3

[![CI](https://github.com/GvPOliveira/almoxarifado-v3/actions/workflows/ci.yml/badge.svg)](https://github.com/GvPOliveira/almoxarifado-v3/actions/workflows/ci.yml)

API REST para controle de estoque de almoxarifado, desenvolvida com **Java 21** e **Spring Boot**.

O projeto nasceu da minha rotina de trabalho em um almoxarifado: entradas por nota fiscal, saídas por requisição, estoque por filial e histórico de movimentações. Esta é a terceira versão do sistema:

| Versão | Tecnologia | Repositório |
| --- | --- | --- |
| v1 / v2 | Java puro → Java + MySQL com **JDBC** (SQL e mapeamento manual) | [almoxarifado-system](https://github.com/GvPOliveira/almoxarifado-system) |
| **v3** | **Spring Boot + Spring Data JPA**, exposto como API REST | este repositório |

O objetivo da v3 é reconstruir o mesmo domínio com o ecossistema Spring e comparar o que antes eu fazia manualmente com JDBC com o que o JPA/Hibernate passa a fazer.

## Status do projeto

🚧 Em desenvolvimento.

| Módulo | Status |
| --- | --- |
| Produtos (cadastro, busca, listagem) | ✅ Pronto, com API e testes |
| Filiais (cadastro, busca, listagem) | ✅ Pronto, com API e testes |
| Produtos da filial (vínculo produto ↔ filial) | ✅ Pronto, com API e testes |
| Fornecedores (cadastro, busca por CNPJ) | ✅ Pronto, com API e testes |
| Notas fiscais (cadastro com itens e total calculado) | ✅ Pronto, com API e testes |
| Entidades e repositórios do domínio completo (estoque por filial, notas fiscais, fornecedores, requisições, movimentações) | ✅ Mapeados com JPA e testados |
| Processamento da nota fiscal (entrada no estoque) | 🔜 Próxima etapa |
| Atendimento e estorno de requisições | 🔜 Planejado |
| Histórico de movimentações via API | 🔜 Planejado |

## Tecnologias

- Java 21
- Spring Boot 4 (Spring Web MVC, Spring Data JPA, Bean Validation)
- Hibernate
- MySQL (desenvolvimento)
- H2 em memória (testes)
- JUnit 5 e MockMvc
- Maven
- HTML, CSS e JavaScript puro (telas simples para testar a API)

## Arquitetura

```text
Controller   → recebe a requisição HTTP, valida a entrada e devolve DTOs
    ↓
Service      → regras de negócio (normalização, duplicidade, validações)
    ↓
Repository   → Spring Data JPA
    ↓
MySQL
```

Decisões principais:

- **DTOs separados das entidades.** A API recebe `*CreateDto` e devolve `*ResponseDto`, ambos declarados como `record`, e a conversão fica em classes `*Mapper`. As entidades JPA nunca são expostas diretamente, o que evita vazar campos internos (como o `id`) e desacopla o contrato da API do banco.
- **Injeção de dependência por construtor** nos controllers e services.
- **Validação em duas camadas.** Bean Validation (`@NotBlank`) no DTO barra a entrada inválida logo na chegada; o service revalida de forma defensiva, porque também pode ser chamado por outras partes do sistema.
- **Códigos normalizados.** Códigos de produto e filial são salvos sem espaços nas pontas e em maiúsculas. Assim, `" abc01 "` e `"ABC01"` são tratados como o mesmo código, evitando duplicidade.

## Tratamento de erros

As exceções de negócio seguem uma hierarquia, e um `@RestControllerAdvice` (`GlobalExceptionHandler`) converte cada tipo no status HTTP correto:

| Exceção base | Exemplos | Status |
| --- | --- | --- |
| `NotFoundException` | `ProductNotFoundException`, `BranchNotFoundException` | `404 Not Found` |
| `ConflictException` | `ProductAlreadyExistsException`, `BranchAlreadyExistsException` | `409 Conflict` |
| `InvalidException` | `ProductInvalidException`, `BranchInvalidException` | `400 Bad Request` |
| Falha de `@Valid` no corpo da requisição | campo vazio ou ausente | `400 Bad Request` |
| Qualquer outra exceção | erro inesperado (registrado em log) | `500 Internal Server Error` |

Com isso, um novo erro de "não encontrado" só precisa estender `NotFoundException` para já retornar 404, sem nenhum `try/catch` nos controllers.

Formato das respostas de erro:

```json
// 404, 409 ou 400 (regra de negócio)
{ "message": "Esse código já existe." }

// 400 (validação de campos)
{ "errors": { "name": "<mensagem da validação>" } }
```

## Endpoints

### Produtos — `/products`

| Método | Rota | Descrição | Sucesso | Erros |
| --- | --- | --- | --- | --- |
| `POST` | `/products` | Cadastra um produto | `201` | `400`, `409` |
| `GET` | `/products/{code}` | Busca pelo código (sem diferenciar maiúsculas/minúsculas) | `200` | `404` |
| `GET` | `/products` | Lista todos | `200` | — |

```http
POST /products
Content-Type: application/json

{ "code": "cab-001", "name": "Cabo de rede Cat6" }
```

```json
// 201 Created
{ "code": "CAB-001", "name": "Cabo de rede Cat6" }
```

### Filiais — `/branches`

| Método | Rota | Descrição | Sucesso | Erros |
| --- | --- | --- | --- | --- |
| `POST` | `/branches` | Cadastra uma filial (código e nome únicos) | `201` | `400`, `409` |
| `GET` | `/branches/{code}` | Busca pelo código | `200` | `404` |
| `GET` | `/branches` | Lista todas | `200` | — |

```http
POST /branches
Content-Type: application/json

{ "code": "sp01", "name": "Filial São Paulo" }
```

```json
// 201 Created
{ "code": "SP01", "name": "Filial São Paulo" }
```

### Produtos da filial — `/branches/{branchCode}/products`

Vincula um produto já cadastrado a uma filial. O vínculo nasce com quantidade `0`: o estoque só muda por movimentação.

| Método | Rota | Descrição | Sucesso | Erros |
| --- | --- | --- | --- | --- |
| `POST` | `/branches/{branchCode}/products` | Vincula um produto à filial (único por filial + produto) | `201` | `400`, `404`, `409` |
| `GET` | `/branches/{branchCode}/products` | Lista os produtos vinculados à filial, ordenados pelo código do produto | `200` | `404` |

A localização é opcional (máx. 15 caracteres), salva sem espaços nas pontas e em maiúsculas. Se vier em branco, é salva como `null`.

```http
POST /branches/sp01/products
Content-Type: application/json

{ "productCode": "cab-001", "location": " a01.p02 " }
```

```json
// 201 Created
{ "productCode": "CAB-001", "branchCode": "SP01", "location": "A01.P02", "quantity": 0 }
```

### Fornecedores — `/suppliers`

| Método | Rota | Descrição | Sucesso | Erros |
| --- | --- | --- | --- | --- |
| `POST` | `/suppliers` | Cadastra um fornecedor (CNPJ único; o nome pode repetir) | `201` | `400`, `409` |
| `GET` | `/suppliers` | Lista todos, em ordem alfabética de nome | `200` | — |
| `GET` | `/suppliers/{cnpj}` | Busca pelo CNPJ | `200` | `404` |

O CNPJ é salvo só com letras e números, em maiúsculas (já aceita o CNPJ alfanumérico), e precisa ter 14 caracteres depois de tirar a pontuação. Na busca, mande o CNPJ sem pontuação: a `/` de um CNPJ formatado quebraria a rota.

```http
POST /suppliers
Content-Type: application/json

{ "name": " Distribuidora Exemplo ", "cnpj": "03.038.044/00ed-01" }
```

```json
// 201 Created
{ "name": "Distribuidora Exemplo", "cnpj": "0303804400ED01" }
```

### Notas fiscais — `/suppliers/{cnpj}/invoices`

A nota fica dentro do fornecedor porque é identificada por número + fornecedor. Ela é salva como **não processada** (`processed: false`): o estoque só muda quando a nota for processada (próxima etapa).

| Método | Rota | Descrição | Sucesso | Erros |
| --- | --- | --- | --- | --- |
| `POST` | `/suppliers/{cnpj}/invoices` | Cadastra uma nota com seus itens | `201` | `400`, `404`, `409` |

Regras:

- O fornecedor, a filial e cada produto precisam existir (`404`).
- Item com destino `STOCK` exige que o produto esteja vinculado à filial da nota (`404`). Item `DIRECT` (consumo direto) não exige.
- O número da nota é único por fornecedor (`409`) e é salvo sem espaços nas pontas.
- A nota precisa de pelo menos um item, e cada item é validado (quantidade e preço maiores que zero, destino obrigatório). O erro de um item aparece com o caminho do campo, por exemplo `items[0].quantity`.
- O total (`totalValue`) não é enviado: é calculado pela própria entidade como a soma de quantidade × preço unitário dos itens.

```http
POST /suppliers/0303804400ed01/invoices
Content-Type: application/json

{
  "number": "000123",
  "branchCode": "sp01",
  "items": [
    { "productCode": "cab-001", "quantity": 3, "unitPrice": 5.20, "destination": "STOCK" },
    { "productCode": "luv-010", "quantity": 2, "unitPrice": 5.00, "destination": "DIRECT" }
  ]
}
```

```json
// 201 Created
{
  "number": "000123",
  "supplierCnpj": "0303804400ED01",
  "branchCode": "SP01",
  "totalValue": 25.60,
  "processed": false,
  "items": [
    { "productCode": "CAB-001", "quantity": 3, "unitPrice": 5.20, "destination": "STOCK" },
    { "productCode": "LUV-010", "quantity": 2, "unitPrice": 5.00, "destination": "DIRECT" }
  ]
}
```

## Domínio

- **Product**: produto, com código único.
- **Branch**: filial, com código e nome únicos.
- **BranchProduct**: um produto dentro de uma filial, com quantidade em estoque e localização (único por filial + produto).
- **Supplier**: fornecedor, identificado pelo CNPJ.
- **Invoice / ProductInvoice**: nota fiscal de entrada e seus itens. O número da nota é único por fornecedor.
- **Request / ProductRequest**: requisição de saída e seus itens, com quantidade solicitada e atendida.
- **Movement**: histórico de movimentações (`ENTRY`, `OUTPUT`, `REVERSAL`).

Itens de nota fiscal podem ter destino `STOCK` (movimenta o estoque) ou `DIRECT` (apenas registrado, sem entrada no estoque).

Principais regras de negócio:

- Um produto pode existir em várias filiais, com quantidade e localização próprias em cada uma.
- A quantidade atendida de uma requisição não pode ser maior que a solicitada.
- Toda movimentação de estoque gera um registro de histórico.
- Um estorno não apaga a movimentação original: registra uma nova movimentação do tipo `REVERSAL`.

## Testes

Os testes rodam com o perfil `test`, que usa um **banco H2 em memória**. Não é preciso ter MySQL instalado para executá-los, e eles não alteram o banco de desenvolvimento.

- **Testes de API (MockMvc)**: produtos, filiais, vínculo de produto à filial, fornecedores e notas fiscais, cobrindo os status 201, 200, 400, 404 e 409, além de casos de borda como códigos que diferem só por maiúsculas ou espaços, CNPJ formatado e validação de itens dentro da lista.
- **Testes unitários de entidade (JUnit puro, sem Spring)**: o cálculo do total da nota fiscal ao adicionar itens.
- **Testes de repositório**: consultas do Spring Data JPA para filiais, estoque por filial, fornecedores, notas fiscais, requisições e movimentações, incluindo os cenários de "não encontrado".

```bash
./mvnw test
```

Os mesmos testes rodam automaticamente no **GitHub Actions** a cada push e pull request na `main` ([workflow](.github/workflows/ci.yml)). O selo no topo deste README mostra o resultado da última execução.

## Como executar

**Pré-requisitos:** Java 21 e MySQL.

1. Crie o banco de dados:

   ```sql
   CREATE DATABASE almoxarifado_v3;
   ```

2. Defina as credenciais do MySQL como variáveis de ambiente (elas não ficam no código):

   ```bash
   # Linux / macOS
   export DB_USERNAME=seu_usuario
   export DB_PASSWORD=sua_senha

   # Windows (PowerShell)
   $env:DB_USERNAME="seu_usuario"
   $env:DB_PASSWORD="sua_senha"
   ```

3. Suba a aplicação (as tabelas são criadas automaticamente pelo Hibernate):

   ```bash
   ./mvnw spring-boot:run
   ```

4. Acesse `http://localhost:8080` para usar as telas de teste, ou chame os endpoints com Postman/Insomnia.

## Estrutura

```text
src/main/java/br/com/almoxarifado
├── controllers    # endpoints REST e GlobalExceptionHandler
├── services       # regras de negócio
├── repositories   # interfaces Spring Data JPA
├── entities       # entidades JPA
├── dtos           # contratos de entrada e saída da API
├── exceptions     # hierarquia: notfound, conflict, invalid
└── enums          # MovementType, OriginType, Destination
```

## O que mudou do JDBC (v2) para o Spring (v3)

| Na v2 (JDBC) | Na v3 (Spring) |
| --- | --- |
| `Connection`, `PreparedStatement` e SQL escritos à mão | Repositórios Spring Data JPA (`findByCode`, `existsByCode`…) |
| Reconstrução manual dos objetos percorrendo o `ResultSet` linha a linha | Relacionamentos mapeados com `@ManyToOne` / `@OneToMany` |
| Script SQL para criar as tabelas | Tabelas geradas a partir das entidades |
| Exceções de negócio tratadas por quem chamava | Exceções convertidas em status HTTP pelo `GlobalExceptionHandler` |

Ter feito a persistência manualmente na v2 me ajudou a entender o que o JPA faz por baixo dos panos.

## Autor

**Gabriel Vinicius**, estudante de Análise e Desenvolvimento de Sistemas (SENAC SP).