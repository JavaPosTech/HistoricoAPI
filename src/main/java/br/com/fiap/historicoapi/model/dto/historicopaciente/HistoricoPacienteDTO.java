package br.com.fiap.historicoapi.model.dto.historicopaciente;

import br.com.fiap.historicoapi.model.entity.historicopaciente.HistoricoPaciente;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

@SchemaMapping("HistoricoPaciente")
@Schema(description = "Representa o modelo de dados de um Histórico de Paciente.")
public record HistoricoPacienteDTO(

        Integer id,

        String queixaPrincipal,

        String historicoDoenca,

        String medicamentos,

        String alergias,

        String observacao

) {
    public HistoricoPacienteDTO(HistoricoPaciente historicoPaciente) {
        this(
                historicoPaciente.getId(),
                historicoPaciente.getQueixaPrincipal(),
                historicoPaciente.getHistoricoDoenca(),
                historicoPaciente.getMedicamentos(),
                historicoPaciente.getAlergias(),
                historicoPaciente.getObservacoes()
        );
    }
}