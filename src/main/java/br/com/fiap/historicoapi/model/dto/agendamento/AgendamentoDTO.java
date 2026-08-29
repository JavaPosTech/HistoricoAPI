package br.com.fiap.historicoapi.model.dto.agendamento;

import br.com.fiap.historicoapi.model.entity.agendamento.Agendamento;
import br.com.fiap.historicoapi.util.FormatadorData;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa o modelo de dados de uma Consulta.")
public record AgendamentoDTO(

        @Schema(description = "Identificador da consulta. No schema GraphQL é um ID! e trafega como String.", example = "1")
        Integer id,

        @Schema(description = "Nome do médico responsável pela consulta", example = "Carlos Andrade")
        String nomeMedico,

        @Schema(description = "Data e hora da consulta, no formato dd/MM/yyyy - HH:mm:ss", example = "20/02/2026 - 09:00:00")
        String dataHoraConsulta,

        @Schema(description = "Observação registrada no agendamento", example = "Consulta de rotina")
        String observacao,

        @Schema(description = "Data do cadastro do agendamento, no formato dd/MM/yyyy - HH:mm:ss", example = "10/01/2026 - 14:35:12")
        String dataCadastro

) {
    public static AgendamentoDTO from(Agendamento agendamento) {
        return new AgendamentoDTO(
                agendamento.getId(),
                agendamento.getMedico().getNome(),
                FormatadorData.formatar(agendamento.getDataHoraConsulta()),
                agendamento.getObservacao(),
                FormatadorData.formatar(agendamento.getDataCadastro())
        );
    }
}
