package br.com.fiap.historicoapi.exceptions.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.net.URI;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Estrutura genérica para respostas de erro da API")
public record ErrorResponseDTO(

        @Schema(description = "Código HTTP", example = "400")
        int status,

        @Schema(description = "Título resumido", example = "Requisição Inválida!")
        String title,

        @Schema(description = "Endpoint da requisição", example = "/HistoricoAPI/graphql")
        String instance,

        @Schema(description = "URI identificadora do tipo de erro", format = "uri-reference", example = "/HistoricoAPI/problems/unreadable-message")
        URI type,

        @Schema(description = "Mensagem detalhada")
        String detail,

        @Schema(description = "Detalhe complementar, presente apenas em alguns erros")
        Object errors,

        @Schema(description = "Data e hora do erro, no formato dd/MM/yyyy - HH:mm:ss", implementation = String.class, example = "12/09/2026 - 23:17:23")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy - HH:mm:ss")
        LocalDateTime timestamp

) {
    public ErrorResponseDTO(int pStatus, String pTitle, String pInstance, String pType, String pDetail, Object pErrors) {
        this(pStatus, pTitle, pInstance, URI.create(pType), pDetail, pErrors, LocalDateTime.now());
    }

    public ErrorResponseDTO(int pStatus, String pTitle, String pInstance, String pType, String pDetail) {
        this(pStatus, pTitle, pInstance, URI.create(pType), pDetail, null, LocalDateTime.now());
    }
}