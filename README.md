<div align="center"> <br> 
  <img align="center" alt="guru-java" height="150" width="150" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/spring/spring-original.svg" />
</div> 

<br> 

<div align="center">
  Turma 12ADJT – Projeto desenvolvido na pós-graduação em Arquitetura e Desenvolvimento em Java da FIAP. O objetivo é desenvolver uma API responsável pelo gerenciamento do histórico de consultas, com armazenamento dos dados e disponibilização das informações por meio de uma interface GraphQL.
</div> 

 <br> <br> 

> 🚧 **Status:** projeto em fase inicial. A estrutura de build, os perfis de execução, o logging e os arquivos de Docker já estão configurados. As entidades, os resolvers GraphQL e a suíte de testes ainda serão implementados.

<br> 

## 🧰 Ferramentas Utilizadas

* 🔄 Lombok

* 📝 Log4j2

* ☕️ Java 21

* 🐘 PostgreSQL 18

* 🧪 JUnit 5 + JaCoCo

* 🟢 Spring Boot 4.0.5

* 🔷 Spring for GraphQL

* 🐳 Docker / Docker Compose

* 🛠️ Gradle 9.7.1 (Kotlin DSL)

* 🗄️ Spring Data JPA + Bean Validation

* 📑 SpringDoc OpenAPI (Swagger UI)

<br> 

## 📁 Estrutura do Projeto

O código é organizado **por camada e, dentro de cada camada, por domínio**:

```
src/main/java/br/com/fiap/historicoapi/
├── config/           # Configurações (banco de dados, Swagger, GraphQL)
├── controller/       # Resolvers GraphQL e endpoints REST
├── service/          # Regras de negócio
├── repository/       # Interfaces JpaRepository
├── model/
│   ├── entity/       # Entidades JPA
│   ├── dto/          # Modelos de saída
│   ├── request/      # Modelos de entrada
│   └── response/     # Envelopes de resposta
├── exceptions/       # Exceções de negócio e GlobalExceptionHandler
└── enums/

src/main/resources/
├── application.yaml  # Perfis dev, prod e test
├── log4j2.xml        # Console em dev; arquivo rotativo em prod
└── graphql/          # Schemas .graphqls
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

## 🛠️ Desenvolvimento 

Para o ambiente de desenvolvimento, o projeto disponibiliza o arquivo `docker-compose-postgres.yml`, já configurado com todas as variáveis necessárias para conexão com o banco de dados. 

Para iniciar o serviço do PostgreSQL, execute no terminal: 

```bash
docker compose -f docker-compose-postgres.yml up -d
```

Em seguida, execute a aplicação utilizando a opção `BootRun - DEV`. Dessa forma, a API será conectada automaticamente ao banco de dados configurado no Docker Compose, facilitando a execução do projeto em ambiente local e ficando disponível na porta `9017`.

> ℹ️ A conexão com o banco é montada a partir das variáveis `DATABASE_IP`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER` e `DATABASE_PASSWORD`. As configurações de execução do IntelliJ (`.run/`) já definem esses valores; ao rodar pelo terminal, exporte-os antes de iniciar a aplicação.

<br> 

## 🚀 Produção

Para execução em ambiente de produção, o projeto disponibiliza o arquivo `docker-compose-historicoapi.yml`. Antes de iniciar a aplicação, é necessário configurar o arquivo `.env` com as variáveis de conexão do banco de dados, conforme o ambiente desejado:

```bash
# DATABASE_PORT
$ Exemplo: 5432

# DATABASE_NAME
$ Exemplo: postgres

# DATABASE_USER
$ Exemplo: postgres

# DATABASE_PASSWORD
$ Exemplo: postgres@2026
```

> ℹ️ Importante: a variável `DATABASE_PORT` representa a porta utilizada pela aplicação para se conectar ao banco de dados dentro da rede interna do Docker.
O valor padrão é `5432`. Caso deseje alterar essa porta no arquivo  `.env`, também será necessário ajustar o arquivo `docker-compose-historicoapi.yml`, atualizando a porta interna do container PostgreSQL para o mesmo valor configurado.

```yaml
ports:
  - "8745:5432"
```

Se alterar `DATABASE_PORT` para `5433`, o mapeamento deverá ser ajustado para:

```yaml
ports:
  - "8745:5433"
```

Nesse exemplo:

* `8745` = porta externa utilizada pelo host para acessar o banco
* `5432` ou `5433` = porta interna utilizada pela API para se conectar ao PostgreSQL

<br> 

Após configurar o arquivo `.env` com as variáveis de conexão do banco de dados, execute no terminal:

```bash
docker compose -f docker-compose-historicoapi.yml up -d
```

Dessa forma, a API será iniciada utilizando as variáveis definidas no arquivo `.env` e ficará disponível na porta `9027`.

> ℹ️ Quando a API é executada em produção, é criada automaticamente uma pasta chamada `logs` no diretório onde a aplicação está sendo executada. Essa pasta é responsável por armazenar todos os logs gerados pela API, sendo organizados de forma diária, ou seja, a cada novo dia é gerado um arquivo específico contendo a data correspondente, facilitando a rastreabilidade e análise das execuções. Além disso, a aplicação possui uma política de limpeza automática, na qual os arquivos de `logs` são mantidos por um período de 30 dias. Após esse prazo, os `logs` mais antigos são excluídos automaticamente, garantindo melhor gerenciamento de armazenamento.

<br> 

## 🌐 GraphQL

Todas as rotas da aplicação são relativas ao context path **`/HistoricoAPI`**. A interface GraphQL é exposta em:

```bash
# Perfil DEV
$ POST http://localhost:9017/HistoricoAPI/graphql

# Perfil PROD
$ POST http://localhost:9027/HistoricoAPI/graphql
```

Os schemas ficam em `src/main/resources/graphql/` e são carregados automaticamente pelo Spring for GraphQL.

> ℹ️ A interface interativa **GraphiQL** vem desabilitada por padrão. Para utilizá-la durante o desenvolvimento, adicione ao bloco do perfil `dev` em `application.yaml`:

```yaml
spring:
  graphql:
    graphiql:
      enabled: true
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
$ http://localhost:9027/HistoricoAPI/swagger-ui/index.html
```

<br> 

## 🧪 Testes

A suíte de testes é composta por **testes de integração reais**: eles sobem o contexto do Spring e se conectam a um PostgreSQL de verdade. Por isso, **o banco precisa estar no ar antes de executar os testes**:

```bash
docker compose -f docker-compose-postgres.yml up -d
./gradlew test
```

Os testes utilizam o perfil `test` e devem ser executados a partir da raiz do projeto.

Ao final da execução, o **JaCoCo** gera o relatório de cobertura em:

```bash
build/reports/jacoco/test/html/index.html
```

> ℹ️ Os pacotes `config`, `enums`, `exceptions` e `model`, além da classe `HistoricoAPIApplication`, são intencionalmente excluídos do cálculo de cobertura.

<br> 

## ⚠️ Observação

Recomenda-se utilizar o IntelliJ IDEA como IDE para este projeto, pois ele já possui configurações prontas para execução e build, como `BootRun - DEV`, `BootRun - PROD` e `Clean Build - [Without Tests]`. Dessa forma, o uso do IntelliJ proporciona uma experiência mais prática e otimizada no desenvolvimento.
