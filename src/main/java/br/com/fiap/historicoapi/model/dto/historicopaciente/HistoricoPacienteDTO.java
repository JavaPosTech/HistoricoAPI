package br.com.fiap.historicoapi.model.dto.historicopaciente;

import br.com.fiap.historicoapi.model.entity.historicopaciente.HistoricoPaciente;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa o modelo de dados de um Histórico de Paciente.")
public record HistoricoPacienteDTO(

        @Schema(description = "Identificador do histórico. No schema GraphQL é um ID! e trafega como String.", example = "1")
        Integer id,

        @Schema(description = "Queixa principal relatada pelo paciente", example = "Dor de cabeça persistente")
        String queixaPrincipal,

        @Schema(description = "Histórico da doença atual", example = "Enxaqueca crônica diagnosticada em 2020")
        String historicoDoenca,

        @Schema(description = "Medicamentos em uso pelo paciente", example = "Dipirona 500mg")
        String medicamentos,

        @Schema(description = "Alergias conhecidas do paciente", example = "Penicilina")
        String alergias,

        @Schema(description = "Observações complementares", example = "Retorno em 30 dias")
        String observacoes

) {
    public static HistoricoPacienteDTO from(HistoricoPaciente historicoPaciente) {
        return new HistoricoPacienteDTO(
                historicoPaciente.getId(),
                historicoPaciente.getQueixaPrincipal(),
                historicoPaciente.getHistoricoDoenca(),
                historicoPaciente.getMedicamentos(),
                historicoPaciente.getAlergias(),
                historicoPaciente.getObservacoes()
        );
    }
}
