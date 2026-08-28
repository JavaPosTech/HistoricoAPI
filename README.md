<div align="center"> <br> 
  <img align="center" alt="guru-java" height="150" width="150" src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/spring/spring-original.svg" />
</div> 

<br> 

<div align="center">
    Turma 12ADJT – Projeto desenvolvido na pós-graduação em Arquitetura e Desenvolvimento em Java da FIAP. O objetivo é desenvolver um sistema hospitalar baseado em microsserviços, com serviços independentes para o gerenciamento de agendamentos e envio de notificações, utilizando Spring Security para autenticação e autorização e comunicação assíncrona entre os serviços.
</div> 

 <br> <br> 

## 🧰 Ferramentas Utilizadas

* 🛠️ Gradle 9.7 (Kotlin DSL)

* ☕️ Java 21

* 🐘 PostgreSQL 18

* 🟢 Spring Boot 4.0.5

* 🔐 Spring Security + JWT (jjwt)

* 🦅 Flyway (versionamento do banco)

* 🔄 MapStruct + Lombok

* 📝 Log4j2

* 📑 SpringDoc OpenAPI (Swagger UI)

* 🧪 JUnit 5 + JaCoCo

* 🐳 Docker / Docker Compose

<br> 

## 📁 Estrutura do Projeto

O código é organizado **por camada e, dentro de cada camada, por domínio** (`paciente`, `medico`, `enfermeiro`, `agendamento`, `usuario`, `auth`):

```
src/main/java/br/com/fiap/agendamentoapi/
├── config/           # DataBaseConfig, SecurityConfig, SwaggerConfig e SecurityFilter
├── controller/       # Endpoints REST
├── service/          # Regras de negócio
├── repository/       # Interfaces JpaRepository
├── model/
│   ├── entity/       # Entidades JPA
│   ├── dto/          # Modelos de saída
│   ├── request/      # Modelos de entrada
│   ├── mapper/       # Mapeamentos MapStruct
│   └── response/     # PageResponse, MensagemSucessoResponse, TokenResponse
├── exceptions/       # Exceções de negócio e GlobalExceptionHandler
└── enums/            # TipoUsuario, SituacaoCadastro

src/main/resources/
├── application.yaml              # Perfis dev, prod e test
├── log4j2.xml                    # Console em dev; arquivo rotativo em prod
└── db/migration/                 # Migrações Flyway (V1.0, V1.1, ...)
```

O esquema do banco é criado **exclusivamente pelo Flyway** — não há `ddl-auto`. Toda alteração de entidade exige uma nova migração `V<versão>__<Descrição>.sql`; migrações já aplicadas nunca devem ser editadas.

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

> ℹ️ A conexão com o banco é montada em `DataBaseConfig` a partir das variáveis `DATABASE_IP`, `DATABASE_PORT`, `DATABASE_NAME`, `DATABASE_USER` e `DATABASE_PASSWORD`. As configurações de execução do IntelliJ (`.run/`) já definem esses valores; ao rodar pelo terminal, exporte-os antes de iniciar a aplicação.

<br> 

## 🚀 Produção

Para execução em ambiente de produção, o projeto disponibiliza o arquivo `docker-compose-agendamentoapi.yml`. Antes de iniciar a aplicação, é necessário configurar o arquivo `.env` com as variáveis de conexão do banco de dados, conforme o ambiente desejado:

```bash
# DATABASE_PORT
$ Exemplo: 5432

# DATABASE_NAME
$ Exemplo: postgres

# DATABASE_USER
$ Exemplo: postgres

# DATABASE_PASSWORD
$ Exemplo: postgres@2026

# JWT_SECRET
$ Exemplo: uma string aleatória com pelo menos 32 caracteres

# JWT_EXPIRATION_MS
$ Exemplo: 86400000 (24 horas)
```

> ⚠️ `JWT_SECRET` e `JWT_EXPIRATION_MS` possuem valores padrão embutidos no código apenas para facilitar o desenvolvimento local. Em produção, defina obrigatoriamente um `JWT_SECRET` próprio — o valor padrão é público, pois está versionado no repositório.

> ℹ️ Importante: a variável `DATABASE_PORT` representa a porta utilizada pela aplicação para se conectar ao banco de dados dentro da rede interna do Docker.
O valor padrão é `5432`. Caso deseje alterar essa porta no arquivo  `.env`, também será necessário ajustar o arquivo `docker-compose-agendamentoapi.yml`, atualizando a porta interna do container PostgreSQL para o mesmo valor configurado.

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

## 🔐 Autenticação

A API utiliza autenticação via **JWT (JSON Web Token)**. Antes de acessar qualquer endpoint protegido, é necessário realizar login para obter um token de acesso.

```bash
# Endpoint de login
POST /v1/auth/login
```

A resposta traz o token que deve ser enviado no header `Authorization` das próximas requisições:

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "tipo": "Bearer"
}
```

```bash
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

O token expira em 24 horas (configurável via `JWT_EXPIRATION_MS`). Após expirar, é necessário realizar login novamente.

> ⚠️ Cadastro (`POST`) de Médico, Paciente e Enfermeiro **não exige token** (autocadastro livre). Todas as demais operações — listar, atualizar e excluir — exigem um usuário autenticado.

<br>

### Sobre as senhas no banco de dados

O projeto utiliza **BCrypt** para armazenar senhas. Isso significa que a senha de um usuário **nunca** fica salva em texto puro no banco — o que aparece na coluna `senha` (por exemplo, `$2y$05$ZpywJEw26dx/wK55JdAE7uSjF00ckF.qZwx4zqlVrKUjVxsIXr66a`) é um **hash criptográfico**, gerado a partir da senha real combinada com um valor aleatório (chamado de "sal"). Esse processo é de mão única: não existe forma de reverter o hash de volta para a senha original.

Para fazer login, você sempre usa a senha **em texto puro** que foi escolhida no cadastro — nunca o hash salvo no banco.

> ℹ️ Hoje, no seed de testes, todos os usuários compartilham o **mesmo hash** no banco — isso é só uma facilidade para os testes locais. Usuários cadastrados normalmente pela API terão hashes diferentes entre si mesmo usando a mesma senha, pois o BCrypt gera um sal aleatório novo a cada cadastro.

<br>

## 🌐 Endpoints

Todas as rotas abaixo são relativas ao context path **`/AgendamentoAPI`**.

| Método   | Rota                  | Autenticação | Descrição                                        |
| -------- | --------------------- | ------------ | ------------------------------------------------ |
| `POST`   | `/v1/auth/login`      | Pública      | Autentica o usuário e devolve o token JWT        |
| `GET`    | `/v1/medico`          | Requerida    | Lista os médicos (paginado)                      |
| `POST`   | `/v1/medico`          | Pública      | Cadastra um médico                               |
| `PATCH`  | `/v1/medico/{id}`     | Requerida    | Atualiza os dados de um médico                   |
| `DELETE` | `/v1/medico/{id}`     | Requerida    | Exclui logicamente um médico                     |
| `GET`    | `/v1/enfermeiro`      | Requerida    | Lista os enfermeiros (paginado)                  |
| `POST`   | `/v1/enfermeiro`      | Pública      | Cadastra um enfermeiro                           |
| `PATCH`  | `/v1/enfermeiro/{id}` | Requerida    | Atualiza os dados de um enfermeiro               |
| `DELETE` | `/v1/enfermeiro/{id}` | Requerida    | Exclui logicamente um enfermeiro                 |
| `GET`    | `/v1/paciente`        | Requerida    | Lista os pacientes (paginado)                    |
| `POST`   | `/v1/paciente`        | Pública      | Cadastra um paciente                             |
| `PATCH`  | `/v1/paciente/{id}`   | Requerida    | Atualiza os dados de um paciente                 |
| `DELETE` | `/v1/paciente/{id}`   | Requerida    | Exclui logicamente um paciente                   |
| `GET`    | `/v1/agendamento`     | Requerida    | Lista as consultas agendadas (paginado)          |
| `POST`   | `/v1/agendamento`     | Requerida    | Agenda uma nova consulta                         |
| `PATCH`  | `/v1/agendamento/{id}`| Requerida    | Reagenda a data e hora de uma consulta           |

> ℹ️ A exclusão é **lógica**: o registro não é removido do banco, apenas tem sua situação de cadastro alterada para `EXCLUIDO`.

> ℹ️ As listagens são paginadas e a numeração começa em **1**. Utilize os parâmetros `page`, `size` e `sort` (por exemplo, `/v1/paciente?page=1&size=20&sort=nome`).

<br> 

## 📑 Swagger

Para acessar a documentação da API, inicie a aplicação utilizando a opção `BootRun - DEV` e acesse o link abaixo no seu navegador.

```bash
# URL para acessar a documentação da API 
$ http://localhost:9017/AgendamentoAPI/swagger-ui/index.html
```

<br> 

Caso inicie a aplicação utilizando a opção `BootRun - PROD` e acesse o link abaixo no seu navegador.

```bash
# URL para acessar a documentação da API 
$ http://localhost:9027/AgendamentoAPI/swagger-ui/index.html
```

> ⚠️ O context path diferencia maiúsculas de minúsculas: utilize `/AgendamentoAPI`, e não `/agendamentoapi`.

<br> 

## 🧪 Testes

A suíte de testes é composta por **testes de integração reais**: eles sobem o contexto do Spring e se conectam a um PostgreSQL de verdade. Por isso, **o banco precisa estar no ar antes de executar os testes**:

```bash
docker compose -f docker-compose-postgres.yml up -d
./gradlew test
```

Os testes utilizam o perfil `test`, que carrega a configuração de banco de `TestDataBaseConfig` (com valores padrão apontando para `localhost:8745`) e executam dentro de transações revertidas ao final de cada caso. Os payloads das requisições dos testes de controller ficam em `src/test/resources/<dominio>/`, lidos por caminho relativo — portanto, execute os testes a partir da raiz do projeto.

Ao final da execução, o **JaCoCo** gera o relatório de cobertura em:

```bash
build/reports/jacoco/test/html/index.html
```

<br> 

## 🔄 Integração Contínua

O workflow em `.github/workflows/workflow.yml` é acionado a cada Pull Request aberto contra a branch `main`. Ele sobe um container PostgreSQL, configura o Java 21 e executa `./gradlew build`, garantindo que a compilação e os testes automatizados passem antes do merge.

<br> 

## ⚠️ Observação

Recomenda-se utilizar o IntelliJ IDEA como IDE para este projeto, pois ele já possui configurações prontas para execução e build, como `BootRun - DEV`, `BootRun - PROD` e `Clean Build - [Without Tests]`. Dessa forma, o uso do IntelliJ proporciona uma experiência mais prática e otimizada no desenvolvimento.
