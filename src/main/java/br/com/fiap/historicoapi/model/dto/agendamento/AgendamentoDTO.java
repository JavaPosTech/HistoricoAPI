package br.com.fiap.historicoapi.model.dto.agendamento;

import br.com.fiap.historicoapi.model.entity.agendamento.Agendamento;
import br.com.fiap.historicoapi.util.FormatadorData;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa o modelo de dados de uma Consulta.")
public record AgendamentoDTO(

        @Schema(description = "Identificador da consulta", implementation = String.class, example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer id,

        @Schema(description = "Nome do médico responsável pela consulta", example = "JOAO", requiredMode = Schema.RequiredMode.REQUIRED)
        String nomeMedico,

        @Schema(description = "Data e hora da consulta, no formato dd/MM/yyyy - HH:mm:ss", example = "22/08/2026 - 08:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        String dataHoraConsulta,

        @Schema(description = "Observação registrada no agendamento", example = "Primeira consulta cardiológica.", types = {"string", "null"})
        String observacao,

        @Schema(description = "Data do cadastro do agendamento, no formato dd/MM/yyyy - HH:mm:ss", example = "20/08/2026 - 09:15:00", requiredMode = Schema.RequiredMode.REQUIRED)
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
