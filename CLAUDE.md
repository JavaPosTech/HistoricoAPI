# CLAUDE.md

Orientações para o Claude Code (claude.ai/code) ao trabalhar neste repositório.

## Visão Geral

**HistoricoAPI** — Tech Challenge da Fase 3 da pós-graduação em Arquitetura e Desenvolvimento em Java da FIAP (turma 12ADJT).

A API é responsável pelo **gerenciamento do histórico de consultas**: armazenar os dados e disponibilizar as informações por meio de uma **interface GraphQL**. Faz parte de um conjunto de serviços da fase (existem projetos irmãos como `AuthAPI` e `gRPC-Server` no diretório pai).

O domínio já está implementado. A API expõe hoje **uma única query GraphQL**:

```graphql
getHistoricoPaciente(pacienteId: ID!): Paciente
```

Ela devolve os dados cadastrais do paciente junto com o histórico clínico (`historico`) e as consultas agendadas (`consultas`). O fluxo é `HistoricoController` → `HistoricoService` → três repositórios (`PacienteRepository`, `HistoricoPacienteRepository`, `AgendamentoRepository`) → `PacienteDTO.from(...)`.

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
| Docs | SpringDoc OpenAPI 3.1.0 (Swagger UI) |
| Boilerplate | Lombok |
| Testes | JUnit 5 + AssertJ + JaCoCo |

Não há Spring Security, Flyway nem MapStruct neste projeto. O `SwaggerConfig` **declara** um security scheme `bearerAuth` (JWT) apenas para fins de documentação — não existe nenhum filtro de autenticação no classpath. Se alguma dessas dependências for necessária, adicione-a explicitamente ao `build.gradle.kts`.

## Comandos

```bash
# Build completo (com testes + relatório JaCoCo)
./gradlew build

# Build sem testes
./gradlew clean build -x test

# Testes (o relatório JaCoCo é gerado automaticamente ao final)
./gradlew test

# Executar um único teste
./gradlew test --tests "br.com.fiap.historicoapi.util.FormatadorDataTest"

# Executar a aplicação
./gradlew bootRun --args="--spring.profiles.active=dev"
```

No Windows use `.\gradlew.bat`. O IntelliJ tem configurações prontas em `.run/`: `BootRun - DEV`, `BootRun - PROD`, `Clean Build - [Without Tests]` e `Testes de Integração` — elas já injetam as variáveis `DATABASE_*` como variáveis de ambiente.

O banco de desenvolvimento sobe com:

```bash
docker compose -f docker-compose-postgres-dev.yml up -d   # PostgreSQL em localhost:8745
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

- `spring.graphql.graphiql.enabled: true` — a GraphiQL está **habilitada em todos os perfis**, inclusive `prod` (`/HistoricoAPI/graphiql`). Se isso não for desejado em produção, mova a chave para o bloco do perfil `dev`.
- `spring.jackson.deserialization.fail-on-unknown-properties: true` — payloads com campos desconhecidos são rejeitados; os DTOs de entrada precisam refletir exatamente o contrato.
- `spring.jpa.open-in-view: false` — não há sessão JPA aberta na camada web. Carregue tudo o que a resposta precisa dentro do serviço/transação (ver "Convenções de Código" abaixo).
- `spring.data.web.pageable.one-indexed-parameters: true` — paginação começa em **1**, não em 0.
- `spring.web.error.include-stacktrace: never` — nunca vaze stacktrace na resposta.
- Encoding forçado em UTF-8.

## Banco de Dados

A conexão é montada em `config/DataBaseConfig` a partir das variáveis de ambiente `DATABASE_IP`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER` e `DATABASE_PASSWORD`. Mantenha esses nomes, pois `.run/`, `.env` e `docker-compose-historicoapi.yml` já dependem deles.

`DataBaseConfig` é anotado com `@Profile({"dev", "prod"})`; nos testes quem fornece o `DataSource` é `config/TestDataBaseConfig` (em `src/test`), que tem valores default (`localhost:8745`, usuário `postgres`, senha `fiap@2026`) alinhados com `docker-compose-postgres-dev.yml` — por isso a suíte roda localmente sem exportar variável nenhuma. Se você alterar a senha em um dos dois arquivos, altere no outro também.

> **Estratégia de schema:** não há `ddl-auto` configurado nem ferramenta de migração no classpath — o Hibernate não cria nem altera nada. As tabelas (`paciente`, `medico`, `usuario`, `agendamento`, `historico_paciente`, `situacao_cadastro`, `tipo_usuario`, todas no schema `public`) são criadas pelas **migrations Flyway da AgendamentoAPI**, que também insere a carga inicial de dados — incluindo o paciente de `id = 1` do qual a suíte de testes depende. As entidades daqui apenas mapeiam esse schema e são usadas somente para leitura. Ver "Docker" para o fluxo de inicialização.

## Estrutura Atual

O padrão adotado é **organizar por camada e, dentro de cada camada, por domínio**:

```
src/main/java/br/com/fiap/historicoapi/
├── config/
│   ├── DataBaseConfig.java        # DataSource dos perfis dev e prod
│   └── SwaggerConfig.java         # OpenAPI + security scheme bearerAuth (documental)
├── controller/
│   └── HistoricoController.java   # @QueryMapping getHistoricoPaciente
├── service/
│   └── HistoricoService.java      # Validação, orquestração dos repositórios e montagem do DTO
├── repository/
│   ├── agendamento/
│   ├── historicopaciente/
│   └── paciente/
├── model/
│   ├── entity/                    # agendamento, historicopaciente, medico, paciente,
│   │                              # situacaocadastro, tipousuario, usuario
│   └── dto/                       # agendamento, historicopaciente, paciente (records)
├── exceptions/
│   ├── dto/                       # ErrorResponseDTO, MethodArgumentNotValidResponseDTO
│   ├── handler/                   # GlobalExceptionHandler (MVC + GraphQL)
│   ├── PacienteNaoEncontradoException.java
│   └── RequisicaoInvalidaException.java
└── util/
    └── FormatadorData.java        # Formatação de LocalDate / LocalDateTime

src/main/resources/
├── application.yaml
├── log4j2.xml
└── graphql/
    ├── query.graphqls             # type Query base
    └── schema.graphqls            # Tipos do domínio + extend type Query
```

Ainda **não existem** os pacotes `model/request/`, `model/response/` e `enums/` — a API é somente de leitura e não recebe payloads. Crie-os apenas se surgir uma mutation.

O Spring for GraphQL carrega automaticamente `classpath:graphql/**/*.graphqls`. O endpoint é `POST /HistoricoAPI/graphql`.

## Convenções de Código

**Persistência com `open-in-view: false`.** Toda associação é `LAZY` e não há sessão aberta na camada web, então qualquer relacionamento que o GraphQL precise devolver tem de ser carregado dentro da transação. O padrão usado é `@EntityGraph` no próprio repositório — inclusive sobrescrevendo métodos herdados:

```java
@Override
@EntityGraph(attributePaths = {"situacaoCadastro"})
Optional<Paciente> findById(@NonNull Integer id);
```

`AgendamentoRepository.findByPacienteId` faz o mesmo para `medico`. Ao adicionar campos ao schema GraphQL, verifique se a associação correspondente está no `@EntityGraph`, sob risco de `LazyInitializationException`.

**DTOs são `record` com factory `from(...)`.** `PacienteDTO`, `AgendamentoDTO` e `HistoricoPacienteDTO` convertem a entidade no próprio DTO. Datas nunca vão como `LocalDate`/`LocalDateTime` para a resposta: passam por `FormatadorData`, que produz `dd/MM/yyyy` e `dd/MM/yyyy - HH:mm:ss`. No schema GraphQL essas datas são `String`.

**Tratamento de erros em dois pipelines.** O pipeline do GraphQL não passa pelo `HandlerExceptionResolver` do Spring MVC, então `GlobalExceptionHandler` mantém dois conjuntos de handlers: os `@ExceptionHandler` (respostas REST com `ErrorResponseDTO`) e os `@GraphQlExceptionHandler` (que devolvem `GraphQLError` com o `ErrorType` correto). **Ao criar uma nova exceção de negócio, registre-a nos dois lugares** — sem o handler GraphQL ela chega ao cliente como `INTERNAL_ERROR` com a mensagem mascarada.

**Validação no serviço.** `HistoricoService.validarPacienteId` rejeita `null` e valores `<= 0` com `RequisicaoInvalidaException`; paciente inexistente vira `PacienteNaoEncontradoException`. Log de entrada e de saída com `@Slf4j`.

**Idioma.** Código, nomes de classes, mensagens e logs em **português**, seguindo o que já existe no repositório e nos projetos irmãos.

## Logging

Log4j2 é a única implementação no classpath: `logback-classic` e `spring-boot-starter-logging` são excluídos em `build.gradle.kts`. Nunca reintroduza Logback e programe sempre contra a API do SLF4J (`LoggerFactory` / `@Slf4j` do Lombok).

`log4j2.xml` usa `<SpringProfile>`: console colorido em todos os perfis; no perfil `prod` também grava em `/app/logs/HistoricoAPI.log`, com rotação diária e expurgo automático após 30 dias. Em produção esse diretório é montado no host via volume `./logs`. O logger `org.hibernate.orm.connections.pooling` está fixado em `ERROR`.

## Testes e Cobertura

A suíte são **testes de integração reais**: sobem o contexto do Spring contra um PostgreSQL de verdade, e não com banco em memória. O banco precisa estar no ar antes de rodar a suíte, e os testes dependem de **dados pré-existentes** — em especial o paciente de `id = 1`.

Classes base em `src/test/java/br/com/fiap/historicoapi/config/`:

- `AbstractTest` — concentra `@Transactional`, `@ActiveProfiles("test")`, `@Import(TestDataBaseConfig.class)` e `@TestMethodOrder`. Base dos testes de serviço, repositório e utilitários.
- `AbstractControllerTest` — o mesmo, mais `@AutoConfigureGraphQlTester` e o helper `executarQuery(documento, nomeVariavel, valor)`.

Convenções ao escrever testes, seguindo o que já existe:

- Herde de `AbstractTest` ou `AbstractControllerTest` e não repita as anotações delas na classe concreta.
- Use `org.junit.jupiter.api.Assertions` de forma qualificada (`Assertions.assertEquals(...)`). O AssertJ está no classpath, mas o padrão do projeto é o `Assertions` do JUnit.
- Nomeie os métodos como `<método><Cenário>Test` — por exemplo `buscarHistoricoPorPacienteIdNullTest`, `formatarLocalDateTimeNullTest`.
- Não use `@Order` nas classes concretas a menos que a ordem realmente importe; a ordenação já vem da classe abstrata.
- Testes que precisam de contexto Spring levam `@SpringBootTest`. Testes puramente unitários (como `FormatadorDataTest`) herdam de `AbstractTest` só por consistência, sem `@SpringBootTest`, e por isso não sobem contexto nem exigem o banco.

**GraphQL nos testes.** `HistoricoController` não expõe rota REST — o `@RequestMapping` na classe não cria endpoint algum. Testar via `MockMvc` em `/v1/historico/{id}` não funciona. O caminho correto é o `GraphQlTester`, passando o id como **variável** da query:

```java
executarQuery(QUERY_HISTORICO_PACIENTE, "pacienteId", 1)
        .path("getHistoricoPaciente")
        .entity(PacienteDTO.class)
        .get();
```

`@AutoConfigureGraphQlTester` executa contra o `ExecutionGraphQlService`, sem camada HTTP — por isso o context path não interfere. Para exercitar o transporte HTTP de verdade, use `@AutoConfigureHttpGraphQlTester` com `@SpringBootTest(webEnvironment = RANDOM_PORT)`.

**Cobertura.** `jacocoTestReport` roda automaticamente depois de `test` e gera o HTML em `build/reports/jacoco/test/html/index.html`. A cobertura **exclui** de propósito `config/`, `enums/`, `exceptions/`, `model/` e a classe `HistoricoAPIApplication` — note que `util/` **não** está excluído e conta no cálculo. Mantenha essa lista alinhada se novos pacotes puramente estruturais forem criados, e atualize a tabela de cobertura do `README.md` quando o número mudar.

Situação atual: 14 testes em 6 classes, com 100% de instruções, linhas, métodos e classes, e 87% de branches. O único branch descoberto é o `pacienteId <= 0` em `HistoricoService.validarPacienteId` — a suíte cobre `null` e id inexistente, mas não um id zero ou negativo.

## Docker

- `Dockerfile` — build multi-stage: `gradle:jdk21-alpine` compila com `./gradlew build -x test`, e o runtime é `eclipse-temurin:21-jre-alpine-3.22`. O artefato copiado é `build/libs/*.jar`; note que a task `jar` (plain jar) está **desabilitada** no `build.gradle.kts`, então só o fat jar do Boot é produzido — não reative a task `jar` sem ajustar esse `COPY`.

São **três** arquivos Compose, e a diferença entre eles importa:

| Arquivo | Papel |
| --- | --- |
| `docker-compose-postgres-dev.yml` | PostgreSQL local com credenciais fixas (`postgres` / `fiap@2026`), sem `.env`. Projeto `fiap-fase3-dev`, volume `fiap-postgres-data-dev`. É o banco do dia a dia e da suíte de testes. |
| `docker-compose-postgres-prod.yml` | PostgreSQL de produção: lê o `.env`, healthcheck `pg_isready`, `TZ: America/Sao_Paulo`. Projeto `fiap-fase3`, volume `fiap-postgres-data`. |
| `docker-compose-historicoapi.yml` | Apenas a API (perfil `prod`, `9028:9027`, volume `./logs`). Declara `shared-net` como rede **externa**. |

**Banco compartilhado entre os microsserviços.** Os dois compose do PostgreSQL criam a rede `shared-net` e são **idênticos aos da AgendamentoAPI**, com `name:` de projeto e de volume fixos — de propósito. Isso garante que o banco iniciado a partir de qualquer repositório da fase é o mesmo container, apontando para o mesmo volume. O banco sobe **uma vez**; os serviços entram na rede depois. Ao alterar um desses dois arquivos, **replique a alteração no repositório irmão**, senão os projetos passam a subir bancos diferentes.

Como os serviços entram numa rede externa, **o banco precisa subir antes** — subir só o compose da API resulta em erro de rede inexistente. Use `up -d --wait` no banco para aguardar o healthcheck.

**Variáveis.** Um único conjunto (`DATABASE_NAME`, `DATABASE_USER`, `DATABASE_PASSWORD`) serve tanto para criar o container do PostgreSQL quanto para os serviços se conectarem, de modo que as credenciais não têm como divergir. `DATABASE_PASSWORD` usa a sintaxe `${DATABASE_PASSWORD:?...}`: se estiver vazia, o Compose aborta com mensagem explícita em vez de criar um banco com senha em branco. `DATABASE_PORT` **não** é lido do `.env` pelos compose — dentro da rede a conexão é sempre `postgres:5432`, valor fixado nos arquivos. A porta `8745` é só a exposição no host, para IDE e cliente SQL.

**Portas dos serviços.** Todas as APIs da fase escutam em `9027` no perfil `prod` dentro do próprio container, então elas se diferenciam pela porta publicada no host: AgendamentoAPI em `9027`, HistoricoAPI em `9028`. Ao adicionar um novo serviço à fase, escolha a próxima porta livre no host e mantenha `9027` do lado do container. Chamadas entre containers usam o nome do container e a porta interna (ex.: `http://HistoricoAPI:9027`).

**Origem do schema.** As migrations Flyway vivem na **AgendamentoAPI** (`src/main/resources/db/migration/`), que cria todas as tabelas da fase — inclusive `paciente`, `historico_paciente` e `agendamento`, lidas por esta API — e insere a carga inicial. Este projeto não tem Flyway nem `ddl-auto`: para ter um banco utilizável (localmente ou em produção), a AgendamentoAPI precisa ter subido ao menos uma vez contra ele. Se uma tabela lida aqui precisar mudar, a migration é escrita **lá**.

## Integração Contínua

`.github/workflows/workflow.yml` roda a cada **Pull Request para `main`**: sobe um service container `postgres:18` em `8745:5432`, configura Java 21 (Temurin) e executa `./gradlew build --no-daemon --info`. Como `build` depende de `test`, **a suíte de integração roda na CI** — qualquer alteração que quebre os testes reprova o PR.

Duas consequências a ter em mente ao mexer nos testes:

- O service container da CI sobe **vazio**, sem o schema e sem os dados que a suíte espera (o paciente de `id = 1`). Qualquer teste que dependa de dados pré-existentes precisa criar seu próprio cenário, ou o workflow precisa de um passo que carregue o schema antes do build.
- O workflow não exporta as variáveis `DATABASE_*`, então a conexão usa os defaults de `TestDataBaseConfig`. A senha do container vem do secret `POSTGRES_PASSWORD`, que portanto precisa bater com o default do `TestDataBaseConfig`.

## Segredos e Arquivos Locais

- `.env` e `.run/` estão no `.gitignore` e contêm credenciais locais — **nunca** faça commit de segredos nem copie senhas desses arquivos para código, README ou documentação.
- `TestDataBaseConfig` e `docker-compose-postgres-dev.yml` versionam a senha de desenvolvimento `fiap@2026`. É credencial descartável de ambiente local, e os dois arquivos precisam continuar em sincronia. Não replique esse padrão em código de produção: lá as credenciais vêm sempre do `.env` / variáveis de ambiente.
- O `.gitattributes` fixa `gradlew` com EOL `lf` e `*.bat` com `crlf`; preserve isso ao editar no Windows.
- `HELP.md` é o arquivo gerado pelo Spring Initializr (e está no `.gitignore`); não é documentação mantida do projeto.
