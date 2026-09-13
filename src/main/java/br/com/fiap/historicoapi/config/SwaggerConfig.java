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
import java.util.Set;

@Configuration
public class SwaggerConfig {

    private static final String TAG_GRAPHQL = "GraphQL";
    private static final String GRAPHQL_PATH = "/graphql";
    private static final String APPLICATION_JSON = "application/json";
    private static final String APPLICATION_GRAPHQL_RESPONSE_JSON = "application/graphql-response+json";
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
                  "nome": "PEDRO",
                  "sobrenome": "ALMEIDA",
                  "cpf": "12345678901",
                  "email": "pedro.almeida@email.com",
                  "telefone": "(19) 99999-1001",
                  "endereco": "Rua das Palmeiras, 50 - Limeira - SP",
                  "dataNascimento": "15/05/1990",
                  "dataCadastro": "20/08/2026 - 09:15:00",
                  "situacaoCadastro": "ATIVO",
                  "historico": [
                    {
                      "id": "1",
                      "queixaPrincipal": "Dor no peito",
                      "historicoDoenca": "Paciente relata dores no peito recorrentes há aproximadamente 2 meses.",
                      "medicamentos": "Losartana 50mg",
                      "alergias": "Nenhuma alergia conhecida.",
                      "observacoes": "Recomendada avaliação cardiológica."
                    }
                  ],
                  "consultas": [
                    {
                      "id": "4",
                      "nomeMedico": "JOAO",
                      "dataHoraConsulta": "22/08/2026 - 14:00:00",
                      "observacao": "Retorno cardiológico.",
                      "dataCadastro": "20/08/2026 - 09:15:00"
                    },
                    {
                      "id": "1",
                      "nomeMedico": "JOAO",
                      "dataHoraConsulta": "22/08/2026 - 08:00:00",
                      "observacao": "Primeira consulta cardiológica.",
                      "dataCadastro": "20/08/2026 - 09:15:00"
                    }
                  ]
                }
              }
            }
            """;

    private static final String EXEMPLO_NAO_ENCONTRADO = """
            {
              "errors": [
                {
                  "message": "Paciente não encontrado - ID: 999",
                  "locations": [{ "line": 1, "column": 51 }],
                  "path": ["getHistoricoPaciente"],
                  "extensions": { "classification": "NOT_FOUND" }
                }
              ],
              "data": {
                "getHistoricoPaciente": null
              }
            }
            """;

    private static final String EXEMPLO_ID_INVALIDO = """
            {
              "errors": [
                {
                  "message": "O ID do Paciente deve ser um número inteiro positivo!",
                  "locations": [{ "line": 1, "column": 51 }],
                  "path": ["getHistoricoPaciente"],
                  "extensions": { "classification": "BAD_REQUEST" }
                }
              ],
              "data": {
                "getHistoricoPaciente": null
              }
            }
            """;

    private static final String EXEMPLO_ID_NAO_NUMERICO = """
            {
              "errors": [
                {
                  "message": "O argumento [pacienteId] possui um valor inválido!",
                  "locations": [{ "line": 1, "column": 51 }],
                  "path": ["getHistoricoPaciente"],
                  "extensions": { "classification": "BAD_REQUEST" }
                }
              ],
              "data": {
                "getHistoricoPaciente": null
              }
            }
            """;

    private static final String EXEMPLO_VARIAVEL_AUSENTE = """
            {
              "errors": [
                {
                  "message": "Variable 'pacienteId' has an invalid value: Variable 'pacienteId' has coerced Null value for NonNull type 'ID!'",
                  "locations": [{ "line": 1, "column": 31 }],
                  "extensions": { "classification": "ValidationError" }
                }
              ]
            }
            """;

    private static final String EXEMPLO_CAMPO_INEXISTENTE = """
            {
              "errors": [
                {
                  "message": "Validation error (FieldUndefined@[getHistoricoPaciente/idade]) : Field 'idade' in type 'Paciente' is undefined",
                  "locations": [{ "line": 1, "column": 41 }],
                  "extensions": { "classification": "ValidationError" }
                }
              ]
            }
            """;

    private static final String EXEMPLO_SINTAXE_INVALIDA = """
            {
              "errors": [
                {
                  "message": "Invalid syntax with offending token '<EOF>' at line 1 column 44",
                  "locations": [{ "line": 1, "column": 44 }],
                  "extensions": { "classification": "InvalidSyntax" }
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
                .servers(List.of(new io.swagger.v3.oas.models.servers.Server()
                        .url("/HistoricoAPI")
                        .description("Context path da aplicação")))
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

                        Toda a comunicação acontece em um único endpoint, **POST /graphql**, que recebe
                        o documento da query no corpo da requisição. Não existem rotas REST: a operação
                        abaixo descreve o transporte HTTP, e o contrato dos dados está nos arquivos
                        .graphqls e nos *schemas* desta página.

                        A API não exige autenticação. Para explorar o schema de forma interativa,
                        utilize a **GraphiQL** em /HistoricoAPI/graphiql.
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
        ModelConverters.getInstance(true)
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
                        em **query**, o nome da operação em **operationName** e as variáveis em
                        **variables**.

                        A única query publicada pelo schema é **getHistoricoPaciente**, que recebe o
                        id do paciente e devolve os dados cadastrais junto com o histórico clínico
                        (historico) e as consultas agendadas (consultas). As datas já vêm formatadas
                        nos padrões dd/MM/yyyy e dd/MM/yyyy - HH:mm:ss, e os campos do tipo ID
                        chegam como texto ("1").

                        ```graphql
                        %s
                        ```

                        Com as variáveis:

                        ```json
                        { "pacienteId": 1 }
                        ```

                        No exemplo do corpo da requisição a query aparece em uma única linha porque o
                        JSON não aceita quebra de linha dentro de uma string. As duas formas são
                        equivalentes para o servidor, mas as posições informadas em **locations** nos
                        erros mudam conforme a formatação enviada.

                        O formato da resposta segue o cabeçalho **Accept**. Com **application/json**,
                        o padrão, todo documento que chega ao GraphQL recebe **200**, e as falhas vêm
                        descritas no array **errors**. Com **application/graphql-response+json**, um
                        documento rejeitado antes da execução, por erro de sintaxe ou de validação,
                        recebe **400**.
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
                .description("Documento GraphQL a ser executado. Propriedades fora do envelope são rejeitadas com **400**.")
                .content(new Content().addMediaType(APPLICATION_JSON, new MediaType()
                        .schema(new Schema<>().$ref(REF_REQUISICAO_GRAPHQL))
                        .addExamples("getHistoricoPaciente", exemplo("Histórico Completo - Paciente: [ID: 1]", exemplo))));
    }

    private ApiResponses respostas() {
        return new ApiResponses()
                .addApiResponse("200", new ApiResponse()
                        .description("""
                                Operação processada pelo GraphQL. A resposta é **200** mesmo quando a
                                execução falha: o motivo vem no array errors, com a classificação em
                                extensions.classification.

                                - **BAD_REQUEST:** id zero, negativo ou não numérico.
                                - **NOT_FOUND:** paciente inexistente.
                                - **INTERNAL_ERROR:** falha inesperada na execução, com a mensagem mascarada.
                                - **ValidationError** e **InvalidSyntax:** documento rejeitado antes da execução,
                                  como id nulo ou ausente, campo inexistente ou query malformada. Nesses casos a
                                  resposta não traz data.
                                """)
                        .content(new Content()
                                .addMediaType(APPLICATION_JSON, respostaGraphQl()
                                        .addExamples("sucesso", exemplo("Paciente encontrado", json(EXEMPLO_SUCESSO)))
                                        .addExamples("pacienteNaoEncontrado", exemplo("Paciente inexistente: NOT_FOUND", json(EXEMPLO_NAO_ENCONTRADO)))
                                        .addExamples("idInvalido", exemplo("Id zero ou negativo: BAD_REQUEST", json(EXEMPLO_ID_INVALIDO)))
                                        .addExamples("idNaoNumerico", exemplo("Id não numérico: BAD_REQUEST", json(EXEMPLO_ID_NAO_NUMERICO)))
                                        .addExamples("variavelAusente", exemplo("Id nulo ou ausente: ValidationError", json(EXEMPLO_VARIAVEL_AUSENTE)))
                                        .addExamples("campoInexistente", exemplo("Campo inexistente: ValidationError", json(EXEMPLO_CAMPO_INEXISTENTE)))
                                        .addExamples("sintaxeInvalida", exemplo("Query malformada: InvalidSyntax", json(EXEMPLO_SINTAXE_INVALIDA))))
                                .addMediaType(APPLICATION_GRAPHQL_RESPONSE_JSON, respostaGraphQl()
                                        .addExamples("sucesso", exemplo("Paciente encontrado", json(EXEMPLO_SUCESSO)))
                                        .addExamples("pacienteNaoEncontrado", exemplo("Paciente inexistente: NOT_FOUND", json(EXEMPLO_NAO_ENCONTRADO)))
                                        .addExamples("idInvalido", exemplo("Id zero ou negativo: BAD_REQUEST", json(EXEMPLO_ID_INVALIDO)))
                                        .addExamples("idNaoNumerico", exemplo("Id não numérico: BAD_REQUEST", json(EXEMPLO_ID_NAO_NUMERICO))))))
                .addApiResponse("400", new ApiResponse()
                        .description("""
                                Requisição recusada antes da execução, em duas situações.

                                - **Corpo inválido:** JSON malformado, propriedade fora do envelope, propriedade
                                  com tipo incompatível ou corpo sem **query**. O erro é tratado pelo
                                  **GlobalExceptionHandler** do Spring MVC e segue o formato ErrorResponseDTO,
                                  qualquer que seja o Accept. O detalhe técnico fica apenas no log da aplicação.
                                - **Documento rejeitado com Accept application/graphql-response+json:** erro de
                                  sintaxe ou de validação, devolvido no envelope GraphQL e sem data.
                                """)
                        .content(new Content()
                                .addMediaType(APPLICATION_JSON, new MediaType()
                                        .schema(new Schema<>().$ref(REF_ERRO))
                                        .addExamples("jsonMalformado", exemplo("JSON malformado", erro(400,
                                                "Requisição Inválida!",
                                                "/HistoricoAPI/problems/unreadable-message",
                                                "O corpo da requisição não é um JSON válido ou não segue o formato de uma requisição GraphQL!")))
                                        .addExamples("propriedadeDesconhecida", exemplo("Propriedade fora do envelope", erro(400,
                                                "Requisição Inválida!",
                                                "/HistoricoAPI/problems/unreadable-message",
                                                "O corpo da requisição não é um JSON válido ou não segue o formato de uma requisição GraphQL!")))
                                        .addExamples("corpoSemQuery", exemplo("Corpo sem query", erro(400,
                                                "Requisição Inválida!",
                                                "/HistoricoAPI/problems/invalid-graphql-request",
                                                "O corpo da requisição não é uma requisição GraphQL válida!"))))
                                .addMediaType(APPLICATION_GRAPHQL_RESPONSE_JSON, respostaGraphQl()
                                        .addExamples("variavelAusente", exemplo("Id nulo ou ausente: ValidationError", json(EXEMPLO_VARIAVEL_AUSENTE)))
                                        .addExamples("campoInexistente", exemplo("Campo inexistente: ValidationError", json(EXEMPLO_CAMPO_INEXISTENTE)))
                                        .addExamples("sintaxeInvalida", exemplo("Query malformada: InvalidSyntax", json(EXEMPLO_SINTAXE_INVALIDA))))))
                .addApiResponse("405", new ApiResponse()
                        .description("""
                                Método HTTP não suportado. O endpoint aceita apenas **POST**, informado no
                                cabeçalho Allow. PUT, PATCH e DELETE recebem o corpo abaixo; GET recebe o
                                405 do próprio Spring for GraphQL, sem corpo.
                                """)
                        .content(new Content().addMediaType(APPLICATION_JSON, new MediaType()
                                .schema(new Schema<>().$ref(REF_ERRO))
                                .example(erro(405,
                                        "Método não permitido!",
                                        "/HistoricoAPI/problems/method-not-allowed",
                                        "O método [PUT] não é suportado por este endpoint!")))))
                .addApiResponse("415", new ApiResponse()
                        .description("""
                                Formato não suportado: Content-Type diferente de application/json, ou Accept
                                sem application/json nem application/graphql-response+json. A resposta vem
                                sem corpo.
                                """))
                .addApiResponse("500", new ApiResponse()
                        .description("""
                                Erro inesperado no processamento da requisição. O detalhe da falha fica apenas
                                no log da aplicação.
                                """)
                        .content(new Content().addMediaType(APPLICATION_JSON, new MediaType()
                                .schema(new Schema<>().$ref(REF_ERRO))
                                .example(erro(500,
                                        "Erro Interno no Servidor!",
                                        "/HistoricoAPI/problems/internal-server-error",
                                        "Falha inesperada ao processar a requisição.")))));
    }

    private MediaType respostaGraphQl() {
        return new MediaType().schema(new Schema<>().$ref(REF_RESPOSTA_GRAPHQL));
    }

    private Example exemplo(String resumo, Object valor) {
        return new Example().summary(resumo).value(valor);
    }

    private Map<String, Object> erro(int status, String title, String type, String detail) {
        var exemplo = new LinkedHashMap<String, Object>();
        exemplo.put("status", status);
        exemplo.put("title", title);
        exemplo.put("instance", "/HistoricoAPI/graphql");
        exemplo.put("type", type);
        exemplo.put("detail", detail);
        exemplo.put("timestamp", "12/09/2026 - 23:17:23");
        return exemplo;
    }

    private Schema<?> schemaRequisicao() {
        return new ObjectSchema()
                .description("Corpo de uma requisição GraphQL sobre HTTP.")
                .addProperty("query", new StringSchema()
                        .description("Documento GraphQL a ser executado."))
                .addProperty("operationName", new Schema<>()
                        .types(Set.of("string", "null"))
                        .description("Nome da operação. Obrigatório apenas quando o documento declara mais de uma."))
                .addProperty("variables", new Schema<>()
                        .types(Set.of("object", "null"))
                        .description("Variáveis do documento, por exemplo { \"pacienteId\": 1 }.")
                        .additionalProperties(Boolean.TRUE))
                .addProperty("extensions", new Schema<>()
                        .types(Set.of("object", "null"))
                        .description("Extensões do protocolo. Não são usadas por esta API.")
                        .additionalProperties(Boolean.TRUE))
                .additionalProperties(Boolean.FALSE)
                .addRequiredItem("query");
    }

    private Schema<?> schemaResposta() {
        return new ObjectSchema()
                .description("Envelope de resposta definido pela especificação GraphQL.")
                .addProperty("data", new Schema<>()
                        .types(Set.of("object", "null"))
                        .description("Resultado da operação. Ausente quando o documento é rejeitado antes da execução.")
                        .addProperty("getHistoricoPaciente", new Schema<>()
                                .oneOf(List.of(
                                        new Schema<>().$ref(REF_PACIENTE),
                                        new Schema<>().types(Set.of("null"))))
                                .description("Nulo quando a execução falha.")))
                .addProperty("errors", new ArraySchema()
                        .items(new Schema<>().$ref(REF_ERRO_GRAPHQL))
                        .description("Presente somente quando a operação produz erros."));
    }

    private Schema<?> schemaErroGraphQl() {
        return new ObjectSchema()
                .description("Erro no formato definido pela especificação GraphQL.")
                .addProperty("message", new StringSchema()
                        .description("Mensagem do erro. Em INTERNAL_ERROR o texto é mascarado e traz apenas um identificador."))
                .addProperty("locations", new ArraySchema()
                        .items(new ObjectSchema()
                                .addProperty("line", new IntegerSchema())
                                .addProperty("column", new IntegerSchema()))
                        .description("Posição do erro dentro do documento enviado."))
                .addProperty("path", new ArraySchema()
                        .items(new StringSchema())
                        .description("Caminho do campo que originou o erro. Ausente nos erros anteriores à execução."))
                .addProperty("extensions", new ObjectSchema()
                        .description("Metadados do erro.")
                        .addProperty("classification", new StringSchema()
                                ._enum(List.of("BAD_REQUEST", "NOT_FOUND", "INTERNAL_ERROR", "ValidationError", "InvalidSyntax"))
                                .description("""
                                        Classificação do erro. **BAD_REQUEST** e **NOT_FOUND** são atribuídas pelo
                                        **GlobalExceptionHandler**, **ValidationError** e **InvalidSyntax** vêm do
                                        próprio GraphQL, e **INTERNAL_ERROR** cobre qualquer outra falha.
                                        """)));
    }

    private Object json(String exemplo) {
        try {
            return new ObjectMapper().readValue(exemplo, Object.class);
        } catch (Exception ex) {
            throw new IllegalStateException("Exemplo OpenAPI inválido!", ex);
        }
    }
}
