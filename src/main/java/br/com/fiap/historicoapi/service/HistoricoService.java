package br.com.fiap.historicoapi.service;

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
    public PacienteDTO getHistoricoById(Integer pacienteId) {
        log.info("Buscando Histórico do Paciente... - ID: [{}]", pacienteId);

        var paciente = getPacienteById(pacienteId);
        var historicoPaciente = getHistoricoPacienteById(pacienteId);
        var historicoConsultas = getHistoricoConsultasById(pacienteId);

        return new PacienteDTO(paciente, historicoPaciente, historicoConsultas);
    }

    private Paciente getPacienteById(Integer pacienteId) {
        return pacienteRepository.findById(pacienteId).orElseThrow(() -> new RuntimeException("Paciente não encontrado - ID: " + pacienteId));
    }

    private List<HistoricoPacienteDTO> getHistoricoPacienteById(Integer pacienteId) {
        var historicoPaciente = historicoPacienteRepository.findByPacienteId(pacienteId);
        return historicoPaciente.stream()
                .map(HistoricoPacienteDTO::new)
                .toList();
    }

    private List<AgendamentoDTO> getHistoricoConsultasById(Integer pacienteId) {
        log.info("Buscando Histórico de Consultas... - Paciente: [ID: {}]", pacienteId);

        var agendamentos = agendamentoRepository.findByPacienteId(pacienteId);
        return agendamentos.stream()
                .map(AgendamentoDTO::new)
                .toList();
    }
}