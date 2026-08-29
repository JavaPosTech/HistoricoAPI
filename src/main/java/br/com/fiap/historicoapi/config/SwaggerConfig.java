package br.com.fiap.historicoapi.config;

import br.com.fiap.historicoapi.exceptions.dto.ErrorResponseDTO;
import br.com.fiap.historicoapi.model.dto.paciente.PacienteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    private static final String TAG_GRAPHQL = "GraphQL";
    private static final String GRAPHQL_PATH = "/graphql";
    private static final String APPLICATION_JSON = "application/json";
    private static final String REF_PACIENTE = Components.COMPONENTS_SCHEMAS_REF + "PacienteDTO";
    private static final String REF_ERRO = Components.COMPONENTS_SCHEMAS_REF + "ErrorResponseDTO";
    private static final String REF_ERRO_GRAPHQL = Components.COMPONENTS_SCHEMAS_REF + "GraphQlError";
    private static final String REF_RESPOSTA_GRAPHQL = Components.COMPONENTS_SCHEMAS_REF + "GraphQlResponse";
    private static final String REF_REQUISICAO_GRAPHQL = Components.COMPONENTS_SCHEMAS_REF + "GraphQlRequest";

    private static final String QUERY_HISTORICO_PACIENTE = """
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
            """;

    private static final String EXEMPLO_SUCESSO = """
            {
              "data": {
                "getHistoricoPaciente": {
                  "id": "1",
                  "nome": "Maria",
                  "sobrenome": "Oliveira",
                  "cpf": "123.456.789-00",
                  "email": "maria.oliveira@email.com",
                  "telefone": "(11) 98765-4321",
                  "endereco": "Rua das Flores, 123 - São Paulo/SP",
                  "dataNascimento": "15/03/1990",
                  "dataCadastro": "10/01/2026 - 14:32:05",
                  "situacaoCadastro": "Ativo",
                  "historico": [
                    {
                      "id": "1",
                      "queixaPrincipal": "Dor de cabeça persistente",
                      "historicoDoenca": "Enxaqueca crônica diagnosticada em 2020",
                      "medicamentos": "Dipirona 500mg",
                      "alergias": "Penicilina",
                      "observacoes": "Retorno em 30 dias"
                    }
                  ],
                  "consultas": [
                    {
                      "id": "1",
                      "nomeMedico": "Carlos Andrade",
                      "dataHoraConsulta": "20/02/2026 - 09:00:00",
                      "observacao": "Consulta de rotina",
                      "dataCadastro": "10/01/2026 - 14:35:12"
                    }
                  ]
                }
              }
            }
            """;

    private static final String EXEMPLO_NAO_ENCONTRADO = """
            {
              "data": {
                "getHistoricoPaciente": null
              },
              "errors": [
                {
                  "message": "Paciente não encontrado - ID: 999",
                  "locations": [{ "line": 2, "column": 5 }],
                  "path": ["getHistoricoPaciente"],
                  "extensions": { "classification": "NOT_FOUND" }
                }
              ]
            }
            """;

    private static final String EXEMPLO_REQUISICAO_INVALIDA = """
            {
              "data": {
                "getHistoricoPaciente": null
              },
              "errors": [
                {
                  "message": "O ID do Paciente deve ser um número inteiro positivo!",
                  "locations": [{ "line": 2, "column": 5 }],
                  "path": ["getHistoricoPaciente"],
                  "extensions": { "classification": "BAD_REQUEST" }
                }
              ]
            }
            """;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(informacoes())
                .tags(List.of(new Tag()
                        .name(TAG_GRAPHQL)
                        .description("Endpoint de transporte HTTP do Spring for GraphQL.")))
                .components(componentes())
                .path(GRAPHQL_PATH, caminhoGraphQl());
    }

    private Info informacoes() {
        return new Info()
                .title("HistoricoAPI")
                .version("1.0.0")
                .description("""
                        API responsável pelo armazenamento do histórico de consultas e pela
                        disponibilização dos dados por meio de uma interface **GraphQL**.
                        
                        Toda a comunicação acontece em um único endpoint — `POST /graphql` — que recebe
                        o documento da query no corpo da requisição. Não existem rotas REST: a operação
                        abaixo descreve o transporte HTTP, e o contrato dos dados está nos arquivos
                        `.graphqls` e nos *schemas* desta página.
                        
                        Para explorar o schema de forma interativa, utilize a **GraphiQL** em
                        `/HistoricoAPI/graphiql`.
                        """);
    }

    private Components componentes() {
        var components = new Components();

        registrarSchema(components, PacienteDTO.class);
        registrarSchema(components, ErrorResponseDTO.class);

        components.addSchemas("GraphQlRequest", schemaRequisicao());
        components.addSchemas("GraphQlResponse", schemaResposta());
        components.addSchemas("GraphQlError", schemaErroGraphQl());

        return components;
    }

    private String queryEmLinhaUnica() {
        return QUERY_HISTORICO_PACIENTE.replaceAll("\\s+", " ").strip();
    }

    private void registrarSchema(Components components, Class<?> classe) {
        ModelConverters.getInstance()
                .readAll(classe)
                .forEach(components::addSchemas);
    }

    private PathItem caminhoGraphQl() {
        return new PathItem().post(new Operation()
                .addTagsItem(TAG_GRAPHQL)
                .operationId("executarOperacaoGraphQl")
                .summary("Executa uma operação GraphQL")
                .description("""
                        Endpoint único da API. O corpo da requisição carrega o documento GraphQL
                        (`query`), o nome da operação (`operationName`) e as variáveis (`variables`).
                        
                        A única query publicada pelo schema é
                        `getHistoricoPaciente(pacienteId: ID!): Paciente`. Ela devolve os dados
                        cadastrais do paciente junto com o histórico clínico (`historico`) e as
                        consultas agendadas (`consultas`). As datas já vêm formatadas nos padrões
                        `dd/MM/yyyy` e `dd/MM/yyyy - HH:mm:ss`.

                        ```graphql
                        %s
                        ```

                        Com as variáveis:

                        ```json
                        { "pacienteId": 1 }
                        ```

                        No exemplo do corpo da requisição a query aparece em uma única linha: o JSON
                        não aceita quebra de linha dentro de uma string, e mantê-la formatada encheria
                        o exemplo de `\\n` escapados. As duas formas são equivalentes para o servidor.

                        Os campos declarados como `ID!` no schema GraphQL trafegam como **String** na
                        resposta (`"1"`), ainda que o schema `PacienteDTO` desta página os descreva
                        como inteiros — a conversão é feita pela própria especificação GraphQL.
                        """.formatted(QUERY_HISTORICO_PACIENTE.strip()))
                .requestBody(corpoRequisicao())
                .responses(respostas()));
    }

    private RequestBody corpoRequisicao() {
        var exemplo = new LinkedHashMap<String, Object>();
        exemplo.put("query", queryEmLinhaUnica());
        exemplo.put("operationName", "BuscarHistoricoPaciente");
        exemplo.put("variables", Map.of("pacienteId", 1));

        return new RequestBody()
                .required(true)
                .description("Documento GraphQL a ser executado.")
                .content(new Content().addMediaType(APPLICATION_JSON, new MediaType()
                        .schema(new Schema<>().$ref(REF_REQUISICAO_GRAPHQL))
                        .addExamples("getHistoricoPaciente", new Example()
                                .summary("Histórico Completo - Paciente: [ID: 1]")
                                .value(exemplo))));
    }

    private ApiResponses respostas() {
        return new ApiResponses()
                .addApiResponse("200", new ApiResponse()
                        .description("""
                                Operação processada. O GraphQL responde `200` mesmo quando a execução
                                falha: nesse caso `data.getHistoricoPaciente` vem `null` e o motivo é
                                descrito no array `errors`, com a classificação em
                                `extensions.classification` — `BAD_REQUEST` para id inválido e
                                `NOT_FOUND` para paciente inexistente.
                                """)
                        .content(new Content().addMediaType(APPLICATION_JSON, new MediaType()
                                .schema(new Schema<>().$ref(REF_RESPOSTA_GRAPHQL))
                                .addExamples("sucesso", new Example()
                                        .summary("Paciente encontrado")
                                        .value(json(EXEMPLO_SUCESSO)))
                                .addExamples("pacienteNaoEncontrado", new Example()
                                        .summary("Paciente inexistente — NOT_FOUND")
                                        .value(json(EXEMPLO_NAO_ENCONTRADO)))
                                .addExamples("requisicaoInvalida", new Example()
                                        .summary("Id nulo, zero ou negativo — BAD_REQUEST")
                                        .value(json(EXEMPLO_REQUISICAO_INVALIDA))))))
                .addApiResponse("400", new ApiResponse()
                        .description("""
                                Corpo malformado ou ilegível. O erro é tratado pelo
                                `GlobalExceptionHandler` do Spring MVC, antes de chegar ao GraphQL.
                                """)
                        .content(conteudoErro(400,
                                "Requisição Inválida!",
                                "/HistoricoAPI/problems/unreadable-message",
                                "JSON parse error: Unexpected end-of-input")))
                .addApiResponse("500", new ApiResponse()
                        .description("Erro inesperado no processamento da requisição.")
                        .content(conteudoErro(500,
                                "Erro Interno no Servidor!",
                                "/HistoricoAPI/problems/internal-server-error",
                                "Falha inesperada ao processar a requisição.")));
    }

    private Content conteudoErro(int status, String title, String type, String detail) {
        var exemplo = new LinkedHashMap<String, Object>();
        exemplo.put("status", status);
        exemplo.put("title", title);
        exemplo.put("instance", "/HistoricoAPI/graphql");
        exemplo.put("type", type);
        exemplo.put("detail", detail);
        exemplo.put("timestamp", "10/01/2026 - 14:32:05");

        return new Content().addMediaType(APPLICATION_JSON, new MediaType()
                .schema(new Schema<>().$ref(REF_ERRO))
                .example(exemplo));
    }

    private Schema<?> schemaRequisicao() {
        return new ObjectSchema()
                .description("Corpo de uma requisição GraphQL sobre HTTP.")
                .addProperty("query", new StringSchema()
                        .description("Documento GraphQL a ser executado."))
                .addProperty("operationName", new StringSchema()
                        .description("Nome da operação. Obrigatório apenas quando o documento declara mais de uma.")
                        .nullable(true))
                .addProperty("variables", new ObjectSchema()
                        .description("Variáveis do documento — por exemplo `{ \"pacienteId\": 1 }`.")
                        .additionalProperties(Boolean.TRUE))
                .addRequiredItem("query");
    }

    private Schema<?> schemaResposta() {
        return new ObjectSchema()
                .description("Envelope de resposta definido pela especificação GraphQL.")
                .addProperty("data", new ObjectSchema()
                        .description("Resultado da operação.")
                        .nullable(true)
                        .addProperty("getHistoricoPaciente", new Schema<>().$ref(REF_PACIENTE)))
                .addProperty("errors", new ArraySchema()
                        .items(new Schema<>().$ref(REF_ERRO_GRAPHQL))
                        .description("Presente somente quando a execução produz erros."));
    }

    private Schema<?> schemaErroGraphQl() {
        return new ObjectSchema()
                .description("Erro no formato definido pela especificação GraphQL.")
                .addProperty("message", new StringSchema()
                        .description("Mensagem do erro."))
                .addProperty("path", new ArraySchema()
                        .items(new StringSchema())
                        .description("Caminho do campo que originou o erro."))
                .addProperty("locations", new ArraySchema()
                        .items(new ObjectSchema()
                                .addProperty("line", new IntegerSchema())
                                .addProperty("column", new IntegerSchema()))
                        .description("Posição do erro dentro do documento enviado."))
                .addProperty("extensions", new ObjectSchema()
                        .description("Metadados do erro.")
                        .addProperty("classification", new StringSchema()
                                ._enum(List.of("BAD_REQUEST", "NOT_FOUND", "INTERNAL_ERROR"))
                                .description("Classificação atribuída pelo `GlobalExceptionHandler`.")));
    }

    private Object json(String exemplo) {
        try {
            return new ObjectMapper().readValue(exemplo, Object.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Exemplo OpenAPI inválido!", ex);
        }
    }
}