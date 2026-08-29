package br.com.fiap.historicoapi.service;

import br.com.fiap.historicoapi.exceptions.PacienteNaoEncontradoException;
import br.com.fiap.historicoapi.exceptions.RequisicaoInvalidaException;
import br.com.fiap.historicoapi.model.dto.agendamento.AgendamentoDTO;
import br.com.fiap.historicoapi.model.dto.historicopaciente.HistoricoPacienteDTO;
import br.com.fiap.historicoapi.model.dto.paciente.PacienteDTO;
import br.com.fiap.historicoapi.model.entity.paciente.Paciente;
import br.com.fiap.historicoapi.repository.agendamento.AgendamentoRepository;
import br.com.fiap.historicoapi.repository.historicopaciente.HistoricoPacienteRepository;
import br.com.fiap.historicoapi.repository.paciente.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final PacienteRepository pacienteRepository;

    private final AgendamentoRepository agendamentoRepository;

    private final HistoricoPacienteRepository historicoPacienteRepository;

    @Transactional(readOnly = true)
    public PacienteDTO buscarHistoricoPorPacienteId(Integer pacienteId) {
        log.info("Buscando Histórico do Paciente... - ID: [{}]", pacienteId);

        validarPacienteId(pacienteId);

        var paciente = buscarPaciente(pacienteId);
        var historicoPaciente = buscarHistoricoPaciente(pacienteId);
        var historicoConsultas = buscarHistoricoConsultas(pacienteId);

        log.info("Histórico do Paciente encontrado! - ID: [{}] | Históricos: [{}] | Consultas: [{}]",
                pacienteId,
                historicoPaciente.size(),
                historicoConsultas.size());

        return PacienteDTO.from(paciente, historicoPaciente, historicoConsultas);
    }

    private void validarPacienteId(Integer pacienteId) {
        if (pacienteId == null || pacienteId <= 0) {
            log.error("ID do Paciente inválido: [{}]", pacienteId);
            throw new RequisicaoInvalidaException("O ID do Paciente deve ser um número inteiro positivo!");
        }
    }

    private Paciente buscarPaciente(Integer pacienteId) {
        return pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new PacienteNaoEncontradoException(pacienteId));
    }

    private List<HistoricoPacienteDTO> buscarHistoricoPaciente(Integer pacienteId) {
        return historicoPacienteRepository.findByPacienteId(pacienteId).stream()
                .map(HistoricoPacienteDTO::from)
                .toList();
    }

    private List<AgendamentoDTO> buscarHistoricoConsultas(Integer pacienteId) {
        return agendamentoRepository.findByPacienteId(pacienteId).stream()
                .map(AgendamentoDTO::from)
                .toList();
    }
}