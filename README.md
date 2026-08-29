<div align="center"> <br> 
  <img align="center" alt="guru-java" height="150" width="150" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/spring/spring-original.svg" />
</div> 

<br> 

<div align="center">
  Turma 12ADJT – Projeto desenvolvido na pós-graduação em Arquitetura e Desenvolvimento em Java da FIAP. O objetivo é desenvolver uma API responsável pelo gerenciamento do histórico de consultas, com armazenamento dos dados e disponibilização das informações por meio de uma interface GraphQL.
</div> 

<br>

<div align="center">
  <img alt="Java 21" src="https://img.shields.io/badge/Java-21-007396?style=flat-square&logo=openjdk&logoColor=white" />
  <img alt="Spring Boot 4.0.5" src="https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?style=flat-square&logo=springboot&logoColor=white" />
  <img alt="PostgreSQL 18" src="https://img.shields.io/badge/PostgreSQL-18-4169E1?style=flat-square&logo=postgresql&logoColor=white" />
  <img alt="Cobertura de linhas 100%" src="https://img.shields.io/badge/cobertura%20de%20linhas-100%25-brightgreen?style=flat-square" />
  <img alt="Cobertura de branches 100%" src="https://img.shields.io/badge/cobertura%20de%20branches-100%25-brightgreen?style=flat-square" />
</div>

 <br> <br> 

## 🧰 Ferramentas Utilizadas

* 📝 Log4j2

* 🦋 Flyway
  
* 🔄 Lombok

* ☕️ Java 21

* 🐘 PostgreSQL 18

* 🗄️ Spring Data JPA

* 🧪 JUnit 5 + JaCoCo

* 🟢 Spring Boot 4.0.5

* 🔷 Spring for GraphQL

* 🔄 GitHub Actions (CI)

* 🛠️ Gradle 9.7.1 (Kotlin DSL)

* 🐳 Docker / Docker Compose

* 📑 SpringDoc OpenAPI (Swagger UI)

<br> 

## 📁 Estrutura do Projeto

O código é organizado **por camada e, dentro de cada camada, por domínio**:

```
src/main/java/br/com/fiap/historicoapi/
├── config/           # DataBaseConfig (perfis dev e prod) e SwaggerConfig
├── controller/       # HistoricoController — resolver GraphQL
├── service/          # HistoricoService — regras de negócio
├── repository/       # Interfaces JpaRepository (paciente, agendamento, histórico)
├── model/
│   ├── entity/       # Entidades JPA
│   └── dto/          # Modelos de saída (records)
├── exceptions/       # Exceções de negócio, DTOs de erro e GlobalExceptionHandler
└── util/             # FormatadorData — formatação de datas

src/main/resources/
├── application.yaml  # Perfis dev, prod e test
├── log4j2.xml        # Console em dev; arquivo rotativo em prod
└── graphql/          # Schemas .graphqls

src/test/resources/
└── db/migration/     # Migrations Flyway (perfil test) — cópia das da AgendamentoAPI
```

<br> 

## ⚙️ Configurações Disponíveis

🔹 `BootRun - DEV`, executa a API no perfil de desenvolvimento, ideal para desenvolvimento local, criação de novas funcionalidades e realização de testes durante a implementação.

<br> 

🔹 `BootRun - PROD`, executa a API utilizando o perfil de produção localmente, permitindo simular o comportamento da aplicação em ambiente de produção.

<br> 

🔹 `Clean Build - [Without Tests]`, realiza o processo de build da aplicação sem executar os testes automatizados, limpando arquivos anteriores e recompilando o projeto de forma mais rápida. 

<br> 

🔹 `Testes de Integração`, executa toda a suíte de testes automatizados do projeto.

<br> 

Caso prefira o terminal, os mesmos comandos estão disponíveis via Gradle Wrapper:

```bash
# Build completo (com testes e relatório de cobertura)
./gradlew build

# Build sem testes
./gradlew clean build -x test

# Executar a suíte de testes
./gradlew test

# Executar a API no perfil desejado
./gradlew bootRun --args="--spring.profiles.active=dev"
```

> ℹ️ No Windows, utilize `.\gradlew.bat` no lugar de `./gradlew`.

<br> 

## 🐳 Banco Compartilhado e Docker Compose

Os microsserviços da Fase 3 compartilham **um único PostgreSQL**. O banco sobe de forma independente, cria a rede `shared-net`, e cada serviço se conecta a ela como rede externa:

```
                       ┌──────────────────────┐
                       │   rede: shared-net   │
                       │                      │
   host:8745  ────────▶│  postgres:5432      │◀──── AgendamentoAPI  (host:9027)
                       │                      │◀──── HistoricoAPI    (host:9028)
                       └──────────────────────┘
```

Dentro da rede, o banco é sempre alcançado pelo hostname **`postgres`** na porta interna **`5432`**. A porta `8745` é apenas a exposição no host, para acesso via IDE ou cliente SQL.

O projeto disponibiliza três arquivos Compose:

| Arquivo | Finalidade |
| --- | --- |
| `docker-compose-postgres-dev.yml` | PostgreSQL de desenvolvimento, com credenciais fixas e sem dependência do `.env`. |
| `docker-compose-postgres-prod.yml` | PostgreSQL de produção: lê o `.env`, possui *healthcheck* e cria a rede `shared-net`. |
| `docker-compose-historicoapi.yml` | Apenas a API, no perfil `prod`, conectando-se à `shared-net` já existente. |

> ℹ️ Os dois arquivos do PostgreSQL são **idênticos aos da AgendamentoAPI** e usam nome de projeto e de volume fixos. Isso significa que tanto faz de qual projeto o banco é iniciado: o container e os dados serão sempre os mesmos. Suba o banco **uma vez**, a partir de qualquer um dos repositórios.

> ⚠️ **Em dev e prod o schema é criado pelas migrations Flyway da AgendamentoAPI.** Esta API apenas lê os dados e não possui `ddl-auto`. Portanto, a AgendamentoAPI precisa ter sido iniciada ao menos uma vez contra o banco antes que a HistoricoAPI funcione nesses perfis.

> ℹ️ **A suíte de testes é exceção:** ela cria o próprio schema. O Flyway está no projeto apenas no classpath de teste e as migrations estão copiadas em `src/test/resources/db/migration/`, então basta um PostgreSQL vazio no ar para rodar `./gradlew test`.

<br> 

## 🛠️ Desenvolvimento 

Para o ambiente de desenvolvimento, o projeto disponibiliza o arquivo `docker-compose-postgres-dev.yml`, já configurado com todas as credenciais necessárias para conexão com o banco de dados, sem exigir nenhuma configuração adicional.

Para iniciar o serviço do PostgreSQL, execute no terminal: 

```bash
docker compose -f docker-compose-postgres-dev.yml up -d --wait
```

Como esta API **não cria o schema**, é necessário iniciar a AgendamentoAPI ao menos uma vez para que suas migrations Flyway criem as tabelas e a carga inicial de dados:

```bash
cd ../AgendamentoAPI
./gradlew bootRun --args="--spring.profiles.active=dev"
```

Feito isso, execute a HistoricoAPI utilizando a opção `BootRun - DEV`. A API será conectada automaticamente ao banco de dados configurado no Docker Compose e ficará disponível na porta `9017`.

> ℹ️ A conexão com o banco é montada a partir das variáveis `DATABASE_IP`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER` e `DATABASE_PASSWORD`. As configurações de execução do IntelliJ (`.run/`) já definem esses valores; ao rodar pelo terminal, exporte-os antes de iniciar a aplicação.

> ℹ️ Ao rodar localmente pela IDE, a aplicação acessa o banco em `localhost:8745`. O valor `5432` só é utilizado pelos containers, que enxergam o PostgreSQL pela rede interna do Docker.

<br> 

## 🚀 Produção

Para execução em ambiente de produção, o projeto disponibiliza os arquivos `docker-compose-postgres-prod.yml` e `docker-compose-historicoapi.yml`. Antes de iniciar a aplicação, é necessário configurar o arquivo `.env` na raiz do projeto:

```bash
# DATABASE_NAME
$ Exemplo: postgres

# DATABASE_USER
$ Exemplo: postgres

# DATABASE_PASSWORD
$ Exemplo: postgres@2026
```

As mesmas variáveis são utilizadas para **criar** o container do PostgreSQL e para a API se **conectar** a ele, de modo que as credenciais não têm como divergir. Se `DATABASE_PASSWORD` não estiver preenchida, o Compose interrompe a execução com uma mensagem explícita, em vez de subir um banco com senha em branco.

> ℹ️ Não é necessário configurar a porta do banco: dentro da rede `shared-net` a conexão é sempre feita em `postgres:5432`, valor já fixado nos arquivos Compose.

<br> 

Após configurar o arquivo `.env`, inicie primeiro o banco de dados e, em seguida, a API:

```bash
# 1. PostgreSQL — também cria a rede shared-net (execute apenas uma vez)
docker compose -f docker-compose-postgres-prod.yml up -d --wait

# 2. HistoricoAPI
docker compose -f docker-compose-historicoapi.yml up -d
```

Dessa forma, a API será iniciada utilizando as variáveis definidas no arquivo `.env` e ficará disponível na porta `9028` do host.

> ⚠️ A aplicação escuta na porta `9027` dentro do container, mas é publicada em `9028` no host, pois a AgendamentoAPI também utiliza a `9027`. Ao acessar a API pelo navegador, utilize `http://localhost:9028`; para chamadas entre containers, utilize `http://HistoricoAPI:9027`.

> ℹ️ Quando a API é executada em produção, é criada automaticamente uma pasta chamada `logs` no diretório onde a aplicação está sendo executada. Essa pasta é responsável por armazenar todos os logs gerados pela API, sendo organizados de forma diária, ou seja, a cada novo dia é gerado um arquivo específico contendo a data correspondente, facilitando a rastreabilidade e análise das execuções. Além disso, a aplicação possui uma política de limpeza automática, na qual os arquivos de `logs` são mantidos por um período de 30 dias. Após esse prazo, os `logs` mais antigos são excluídos automaticamente, garantindo melhor gerenciamento de armazenamento.

<br> 

## 🌐 GraphQL

Todas as rotas da aplicação são relativas ao context path **`/HistoricoAPI`**. A interface GraphQL é exposta em:

```bash
# Perfil DEV
$ POST http://localhost:9017/HistoricoAPI/graphql

# Perfil PROD (container publicado na porta 9028 do host)
$ POST http://localhost:9028/HistoricoAPI/graphql
```

Os schemas ficam em `src/main/resources/graphql/` e são carregados automaticamente pelo Spring for GraphQL.

<br> 

### 🔎 Consulta disponível

A API expõe a query `getHistoricoPaciente`, que retorna os dados cadastrais do paciente junto com o histórico clínico e as consultas agendadas:

```graphql
query BuscarHistoricoPaciente($pacienteId: ID!) {
    getHistoricoPaciente(pacienteId: $pacienteId) {
        id
        nome
        sobrenome
        cpf
        email
        telefone
        endereco
        dataNascimento
        dataCadastro
        situacaoCadastro
        historico {
            id
            queixaPrincipal
            historicoDoenca
            medicamentos
            alergias
            observacoes
        }
        consultas {
            id
            nomeMedico
            dataHoraConsulta
            observacao
            dataCadastro
        }
    }
}
```

Com as variáveis:

```json
{ "pacienteId": 1 }
```

> ℹ️ As datas são retornadas já formatadas, no padrão `dd/MM/yyyy` e `dd/MM/yyyy - HH:mm:ss`.

<br> 

### 🖥️ GraphiQL

A interface interativa **GraphiQL** está habilitada e pode ser utilizada diretamente no navegador:

```bash
# Perfil DEV
$ http://localhost:9017/HistoricoAPI/graphiql

# Perfil PROD (container publicado na porta 9028 do host)
$ http://localhost:9028/HistoricoAPI/graphiql
```

> ℹ️ As listagens paginadas utilizam numeração iniciada em **1**, e não em 0.

> ⚠️ O context path diferencia maiúsculas de minúsculas: utilize `/HistoricoAPI`, e não `/historicoapi`.

<br> 

## 📑 Swagger

Para acessar a documentação da API, inicie a aplicação utilizando a opção `BootRun - DEV` e acesse o link abaixo no seu navegador.

```bash
# URL para acessar a documentação da API 
$ http://localhost:9017/HistoricoAPI/swagger-ui/index.html
```

<br> 

Caso inicie a aplicação utilizando a opção `BootRun - PROD` e acesse o link abaixo no seu navegador.

```bash
# URL para acessar a documentação da API 
$ http://localhost:9028/HistoricoAPI/swagger-ui/index.html
```

<br> 

O documento OpenAPI bruto, caso queira importá-lo em outra ferramenta, fica em `/HistoricoAPI/v3/api-docs`.

<br> 

### 📌 O que está documentado

Como a API **não expõe rotas REST**, o SpringDoc não teria nada a inspecionar por conta própria: a Swagger UI subiria com a mensagem *"No operations defined in spec"*. Por isso a classe `SwaggerConfig` declara manualmente o endpoint de transporte do GraphQL, e a documentação passa a cobrir:

| Item | Descrição |
| --- | --- |
| `POST /graphql` | Endpoint único da API, com o corpo da requisição (`query`, `operationName` e `variables`) e um exemplo pronto da query `getHistoricoPaciente`. |
| `GraphQlRequest` | Formato do corpo de uma requisição GraphQL sobre HTTP. |
| `GraphQlResponse` | Envelope `{ data, errors }` definido pela especificação GraphQL. |
| `GraphQlError` | Estrutura de erro do GraphQL, incluindo `extensions.classification`. |
| `PacienteDTO`, `HistoricoPacienteDTO`, `AgendamentoDTO` | Modelos de saída, resolvidos automaticamente a partir das anotações `@Schema` dos próprios records. |
| `ErrorResponseDTO` | Resposta de erro do `GlobalExceptionHandler` do Spring MVC, usada nos códigos `400` e `500`. |

A resposta `200` traz três exemplos — paciente encontrado, paciente inexistente (`NOT_FOUND`) e id inválido (`BAD_REQUEST`) —, o que deixa explícito o comportamento do GraphQL de responder `200` mesmo quando a execução falha, sinalizando o problema no array `errors`.


> ⚠️ Os campos declarados como `ID!` no schema GraphQL trafegam como **String** na resposta (`"1"`), embora os *schemas* da página os descrevam como inteiros — a conversão é feita pela própria especificação GraphQL.

> ℹ️ A API **não exige autenticação** e a documentação não declara nenhum *security scheme*: não há Spring Security no classpath nem filtro de autenticação neste projeto.

<br> 

## 🧪 Testes

A suíte de testes é composta por **testes de integração reais**: eles sobem o contexto do Spring e se conectam a um PostgreSQL de verdade. Por isso, **o banco precisa estar no ar antes de executar os testes** — mas pode estar completamente vazio:

```bash
docker compose -f docker-compose-postgres-dev.yml up -d --wait
./gradlew test
```

Os testes utilizam o perfil `test` e devem ser executados a partir da raiz do projeto.

<br> 

### 🦋 Flyway no perfil de testes

O schema e a carga de dados que a suíte precisa são criados pelo **Flyway**, restrito ao perfil `test`:

- A dependência entra no `build.gradle.kts` como `testImplementation`, ou seja, o Flyway **não existe no classpath de runtime** — o *fat jar* do Boot não contém nenhuma classe dele, e dev e prod não têm como migrar nada.
- No `application.yaml`, `spring.flyway.enabled` é `false` no documento raiz e `true` apenas no documento do perfil `test`.
- As migrations ficam em `src/test/resources/db/migration/`.

Isso elimina a dependência de subir a AgendamentoAPI antes de rodar os testes, e faz a suíte funcionar na CI, onde o container do PostgreSQL sobe vazio.

> ⚠️ Os arquivos `V1.0__CreateTables.sql` e `V1.1__Inserts.sql` são **cópias byte a byte** das migrations da AgendamentoAPI, e precisam continuar assim. Como o banco de desenvolvimento é compartilhado, a suíte pode encontrar um `flyway_schema_history` criado pela AgendamentoAPI; qualquer diferença no conteúdo mudaria o *checksum* e faria a validação do Flyway falhar. Ao alterar uma migration lá, recopie o arquivo para cá sem editar nada.

Os dois cenários reais funcionam sem configuração adicional: em um **banco vazio** o Flyway aplica as duas migrations e insere a carga inicial; em um **banco já migrado pela AgendamentoAPI** ele reconhece o histórico e não faz nada, e os testes rodam sobre os dados existentes.

<br> 

### 📊 Cobertura

A suíte conta atualmente com **28 testes distribuídos em 7 classes**, cobrindo o resolver GraphQL (pelo `GraphQlTester` e pelo transporte HTTP real), os caminhos de erro `NOT_FOUND` e `BAD_REQUEST`, a camada de serviço, os três repositórios e o utilitário de formatação de datas.

| Métrica | Cobertura |
| --- | --- |
| 📌 Instruções | **100%** |
| 📏 Linhas | **100%** |
| 🔧 Métodos | **100%** |
| 📦 Classes | **100%** |
| 🔀 Branches | **100%** |

Ao final da execução, o **JaCoCo** gera o relatório completo em:

```bash
build/reports/jacoco/test/html/index.html
```

> ℹ️ Os pacotes `config`, `enums`, `exceptions` e `model`, além da classe `HistoricoAPIApplication`, são intencionalmente excluídos do cálculo de cobertura, por serem estruturais e não conterem regra de negócio.

<br> 

## 🔄 Integração Contínua

O workflow `.github/workflows/workflow.yml` é executado a cada **Pull Request** direcionado à branch `main`. Ele provisiona um container PostgreSQL 18 como serviço, configura o Java 21 (Temurin) e executa o build completo:

```bash
./gradlew build --no-daemon --info
```

Como o `build` também roda a suíte de testes, o Pull Request só fica verde se todos os testes passarem.

> ℹ️ O container do PostgreSQL sobe vazio na CI, sem schema e sem dados. Quem prepara o banco é o **Flyway do perfil `test`**, durante a própria execução da suíte — por isso o workflow não precisa de nenhum passo extra de carga.

> ℹ️ A senha do banco utilizado pelo workflow vem do *secret* `POSTGRES_PASSWORD`, configurado nas *Settings* do repositório.

<br> 

## ⚠️ Observação

Recomenda-se utilizar o IntelliJ IDEA como IDE para este projeto, pois ele já possui configurações prontas para execução e build, como `BootRun - DEV`, `BootRun - PROD` e `Clean Build - [Without Tests]`. Dessa forma, o uso do IntelliJ proporciona uma experiência mais prática e otimizada no desenvolvimento.
