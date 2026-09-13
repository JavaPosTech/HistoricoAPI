package br.com.fiap.historicoapi.model.dto.historicopaciente;

import br.com.fiap.historicoapi.model.entity.historicopaciente.HistoricoPaciente;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa o modelo de dados de um Histórico de Paciente.")
public record HistoricoPacienteDTO(

        @Schema(description = "Identificador do histórico", implementation = String.class, example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer id,

        @Schema(description = "Queixa principal relatada pelo paciente", example = "Dor no peito", types = {"string", "null"})
        String queixaPrincipal,

        @Schema(description = "Histórico da doença atual", example = "Paciente relata dores no peito recorrentes há aproximadamente 2 meses.", requiredMode = Schema.RequiredMode.REQUIRED)
        String historicoDoenca,

        @Schema(description = "Medicamentos em uso pelo paciente", example = "Losartana 50mg", requiredMode = Schema.RequiredMode.REQUIRED)
        String medicamentos,

        @Schema(description = "Alergias conhecidas do paciente", example = "Nenhuma alergia conhecida.", requiredMode = Schema.RequiredMode.REQUIRED)
        String alergias,

        @Schema(description = "Observações complementares", example = "Recomendada avaliação cardiológica.", types = {"string", "null"})
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
