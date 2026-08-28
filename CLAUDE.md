# CLAUDE.md

Orientações para o Claude Code (claude.ai/code) ao trabalhar neste repositório.

## Visão Geral

**HistoricoAPI** — Tech Challenge da Fase 3 da pós-graduação em Arquitetura e Desenvolvimento em Java da FIAP (turma 12ADJT).

A API é responsável pelo **gerenciamento do histórico de consultas**: armazenar os dados e disponibilizar as informações por meio de uma **interface GraphQL**. Faz parte de um conjunto de serviços da fase (existem projetos irmãos como `AuthAPI` e `gRPC-Server` no diretório pai).

> ⚠️ **Estado atual:** o projeto está em fase inicial. Existe apenas `HistoricoAPIApplication.java` — não há entidades, repositórios, resolvers GraphQL, schema `.graphqls`, configuração de datasource nem testes. As instruções abaixo definem as convenções a seguir conforme o código for escrito.

## Stack

| Item | Versão / Observação |
| --- | --- |
| Java | 21 (via toolchain do Gradle) |
| Gradle | 9.7.1, Kotlin DSL (`build.gradle.kts`) |
| Spring Boot | 4.0.5 |
| Banco | PostgreSQL 18 |
| API | Spring for GraphQL + Spring MVC (WebMVC) |
| Persistência | Spring Data JPA |
| Logging | Log4j2 (SLF4J) — Logback **excluído** do classpath |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Boilerplate | Lombok |
| Testes | JUnit 5 + JaCoCo |

Não há Spring Security, Flyway nem MapStruct neste projeto. Se alguma dessas dependências for necessária, adicione-a explicitamente ao `build.gradle.kts`.

## Comandos

```bash
# Build completo (com testes + relatório JaCoCo)
./gradlew build

# Build sem testes
./gradlew clean build -x test

# Testes (o relatório JaCoCo é gerado automaticamente ao final)
./gradlew test

# Executar um único teste
./gradlew test --tests "br.com.fiap.historicoapi.MinhaClasseTest"

# Executar a aplicação
./gradlew bootRun --args="--spring.profiles.active=dev"
```

No Windows use `.\gradlew.bat`. O IntelliJ tem configurações prontas em `.run/`: `BootRun - DEV`, `BootRun - PROD`, `Clean Build - [Without Tests]` e `Testes de Integração` — elas já injetam as variáveis `DATABASE_*` como variáveis de ambiente.

O banco de desenvolvimento sobe com:

```bash
docker compose -f docker-compose-postgres.yml up -d   # PostgreSQL em localhost:8745
```

## Perfis e Portas

`src/main/resources/application.yaml` é um arquivo multi-documento com três perfis:

| Perfil | Porta | Características |
| --- | --- | --- |
| `dev` | 9017 | `format_sql`, `org.hibernate.SQL` em DEBUG, binder em TRACE |
| `prod` | 9027 | Log em arquivo rotativo (ver Log4j2) |
| `test` | — | Perfil dos testes de integração |

O **context path é `/HistoricoAPI`** e diferencia maiúsculas de minúsculas. Toda URL da aplicação é prefixada por ele (ex.: `http://localhost:9017/HistoricoAPI/graphql`).

Configurações globais que valem como convenção do projeto:

- `spring.jackson.deserialization.fail-on-unknown-properties: true` — payloads com campos desconhecidos são rejeitados; os DTOs de entrada precisam refletir exatamente o contrato.
- `spring.jpa.open-in-view: false` — não há sessão JPA aberta na camada web. Carregue tudo o que a resposta precisa dentro do serviço/transação, atenção especial com resolvers GraphQL e associações `LAZY`.
- `spring.data.web.pageable.one-indexed-parameters: true` — paginação começa em **1**, não em 0.
- `spring.web.error.include-stacktrace: never` — nunca vaze stacktrace na resposta.
- Encoding forçado em UTF-8.

## Banco de Dados

A conexão é montada a partir das variáveis de ambiente `DATABASE_IP`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER` e `DATABASE_PASSWORD`. **Ainda não existe a classe de configuração que as consome** — ao criá-la (convenção do projeto irmão: `config/DataBaseConfig`), mantenha esses mesmos nomes de variável, pois `.run/`, `.env` e `docker-compose-historicoapi.yml` já dependem deles.

Não há `ddl-auto` configurado e não há ferramenta de migração no classpath. Defina a estratégia de criação de schema antes de escrever as entidades e documente a escolha aqui.

## Estrutura Esperada

O padrão adotado pelo autor nos projetos da fase é **organizar por camada e, dentro de cada camada, por domínio**:

```
src/main/java/br/com/fiap/historicoapi/
├── config/           # Configurações (datasource, Swagger, GraphQL)
├── controller/       # Controllers REST e/ou @Controller com @QueryMapping/@MutationMapping
├── service/          # Regras de negócio
├── repository/       # Interfaces JpaRepository
├── model/
│   ├── entity/       # Entidades JPA
│   ├── dto/          # Modelos de saída
│   ├── request/      # Modelos de entrada
│   └── response/     # Envelopes de resposta
├── exceptions/       # Exceções de negócio + GlobalExceptionHandler
└── enums/

src/main/resources/
├── application.yaml
├── log4j2.xml
└── graphql/          # Schemas .graphqls (local padrão do Spring for GraphQL)
```

O Spring for GraphQL carrega automaticamente `classpath:graphql/**/*.graphqls`. O endpoint padrão é `POST /HistoricoAPI/graphql`; a GraphiQL **não está habilitada** — para usá-la em `dev`, defina `spring.graphql.graphiql.enabled: true` no bloco do perfil.

## Logging

Log4j2 é a única implementação no classpath: `logback-classic` e `spring-boot-starter-logging` são excluídos em `build.gradle.kts`. Nunca reintroduza Logback e programe sempre contra a API do SLF4J (`LoggerFactory` / `@Slf4j` do Lombok).

`log4j2.xml` usa `<SpringProfile>`: console colorido em todos os perfis; no perfil `prod` também grava em `/app/logs/HistoricoAPI.log`, com rotação diária e expurgo automático após 30 dias. Em produção esse diretório é montado no host via volume `./logs`.

## Testes e Cobertura

As dependências de teste são os starters `*-test` do Spring Boot (webmvc, graphql, data-jpa, validation) — o padrão do autor nesta fase são **testes de integração reais**, que sobem o contexto do Spring contra um PostgreSQL de verdade, e não testes com banco em memória. Por isso o banco precisa estar no ar antes de rodar a suíte.

`jacocoTestReport` roda automaticamente depois de `test` e gera o HTML em `build/reports/jacoco/test/html/index.html`. A cobertura **exclui** de propósito `config/`, `enums/`, `exceptions/`, `model/` e a classe `HistoricoAPIApplication` — mantenha essa lista alinhada se novos pacotes puramente estruturais forem criados.

## Docker

- `Dockerfile` — build multi-stage: `gradle:jdk21-alpine` compila com `./gradlew build -x test`, e o runtime é `eclipse-temurin:21-jre-alpine`. O artefato copiado é `build/libs/*.jar`; note que a task `jar` (plain jar) está **desabilitada** no `build.gradle.kts`, então só o fat jar do Boot é produzido — não reative a task `jar` sem ajustar esse `COPY`.
- `docker-compose-historicoapi.yml` — stack de produção (PostgreSQL + API no perfil `prod`, porta 9027), lê o `.env` da raiz.
- `docker-compose-postgres.yml` — apenas o PostgreSQL para desenvolvimento local, exposto em `8745`.

## Convenções

- Código, nomes de classes e mensagens em **português**, seguindo o que já existe no repositório e nos projetos irmãos.
- `.env` e `.run/` estão no `.gitignore` e contêm credenciais locais — **nunca** faça commit de segredos nem copie senhas desses arquivos para código, README ou documentação.
- O `.gitattributes` fixa `gradlew` com EOL `lf` e `*.bat` com `crlf`; preserve isso ao editar no Windows.
- `HELP.md` é o arquivo gerado pelo Spring Initializr (e está no `.gitignore`); não é documentação mantida do projeto.
