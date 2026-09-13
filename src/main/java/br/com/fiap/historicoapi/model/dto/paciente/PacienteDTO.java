package br.com.fiap.historicoapi.model.dto.paciente;

import br.com.fiap.historicoapi.model.dto.agendamento.AgendamentoDTO;
import br.com.fiap.historicoapi.model.dto.historicopaciente.HistoricoPacienteDTO;
import br.com.fiap.historicoapi.model.entity.paciente.Paciente;
import br.com.fiap.historicoapi.util.FormatadorData;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Representa o modelo de dados de um Paciente.")
public record PacienteDTO(

        @Schema(description = "Identificador do paciente", implementation = String.class, example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Integer id,

        @Schema(description = "Primeiro nome do paciente", example = "PEDRO", requiredMode = Schema.RequiredMode.REQUIRED)
        String nome,

        @Schema(description = "Sobrenome do paciente", example = "ALMEIDA", requiredMode = Schema.RequiredMode.REQUIRED)
        String sobrenome,

        @Schema(description = "CPF do paciente, somente números", example = "12345678901", requiredMode = Schema.RequiredMode.REQUIRED)
        String cpf,

        @Schema(description = "E-mail do paciente", example = "pedro.almeida@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @Schema(description = "Telefone de contato do paciente", example = "(19) 99999-1001", requiredMode = Schema.RequiredMode.REQUIRED)
        String telefone,

        @Schema(description = "Endereço do paciente", example = "Rua das Palmeiras, 50 - Limeira - SP", requiredMode = Schema.RequiredMode.REQUIRED)
        String endereco,

        @Schema(description = "Data de nascimento, no formato dd/MM/yyyy", example = "15/05/1990", requiredMode = Schema.RequiredMode.REQUIRED)
        String dataNascimento,

        @Schema(description = "Data do cadastro, no formato dd/MM/yyyy - HH:mm:ss", example = "20/08/2026 - 09:15:00", requiredMode = Schema.RequiredMode.REQUIRED)
        String dataCadastro,

        @Schema(description = "Descrição da situação do cadastro", example = "ATIVO", requiredMode = Schema.RequiredMode.REQUIRED)
        String situacaoCadastro,

        @Schema(description = "Histórico clínico do paciente. Lista vazia quando não há registros", requiredMode = Schema.RequiredMode.REQUIRED)
        List<HistoricoPacienteDTO> historico,

        @Schema(description = "Consultas agendadas para o paciente. Lista vazia quando não há registros", requiredMode = Schema.RequiredMode.REQUIRED)
        List<AgendamentoDTO> consultas

) {
    public PacienteDTO {
        historico = historico == null ? List.of() : List.copyOf(historico);
        consultas = consultas == null ? List.of() : List.copyOf(consultas);
    }

    public static PacienteDTO from(Paciente paciente,
                                   List<HistoricoPacienteDTO> historico,
                                   List<AgendamentoDTO> consultas) {
        return new PacienteDTO(
                paciente.getId(),
                paciente.getNome(),
                paciente.getSobrenome(),
                paciente.getCpf(),
                paciente.getEmail(),
                paciente.getTelefone(),
                paciente.getEndereco(),
                FormatadorData.formatar(paciente.getDataNascimento()),
                FormatadorData.formatar(paciente.getDataCadastro()),
                paciente.getSituacaoCadastro().getDescricao(),
                historico,
                consultas
        );
    }
}
