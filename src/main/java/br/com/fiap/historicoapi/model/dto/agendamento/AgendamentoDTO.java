package br.com.fiap.historicoapi.model.dto.agendamento;

import br.com.fiap.historicoapi.model.entity.agendamento.Agendamento;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

import java.time.LocalDateTime;

@SchemaMapping("Agendamento")
@Schema(description = "Representa o modelo de dados de uma Consulta.")
public record AgendamentoDTO(

        Integer id,

        String nomeMedico,

        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
        LocalDateTime dataHoraConsulta,

        String observacao,

        @JsonFormat(pattern = "dd/MM/yyyy - HH:mm:ss")
        LocalDateTime dataCadastro

) {
    public AgendamentoDTO(Agendamento agendamento) {
        this(agendamento.getId(),
                agendamento.getMedico().getNome(),
                agendamento.getDataHoraConsulta(),
                agendamento.getObservacao(),
                agendamento.getDataCadastro()
        );
    }
}