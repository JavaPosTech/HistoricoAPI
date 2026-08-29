package br.com.fiap.historicoapi.model.dto.historicopaciente;

import br.com.fiap.historicoapi.model.entity.historicopaciente.HistoricoPaciente;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representa o modelo de dados de um Histórico de Paciente.")
public record HistoricoPacienteDTO(

        Integer id,

        String queixaPrincipal,

        String historicoDoenca,

        String medicamentos,

        String alergias,

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