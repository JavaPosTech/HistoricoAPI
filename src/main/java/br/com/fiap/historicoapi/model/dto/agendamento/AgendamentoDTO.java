package br.com.fiap.historicoapi.model.dto.agendamento;

import br.com.fiap.historicoapi.model.entity.agendamento.Agendamento;
import br.com.fiap.historicoapi.util.FormatadorData;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa o modelo de dados de uma Consulta.")
public record AgendamentoDTO(

        Integer id,

        String nomeMedico,

        String dataHoraConsulta,

        String observacao,

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