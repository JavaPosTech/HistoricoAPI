package br.com.fiap.historicoapi.controller;

import br.com.fiap.historicoapi.model.dto.paciente.PacienteDTO;
import br.com.fiap.historicoapi.service.HistoricoService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/historico")
public class HistoricoController {

    private final HistoricoService historicoService;

    @QueryMapping
    public PacienteDTO getHistoricoPaciente(@Argument Integer pacienteId) {
        return historicoService.buscarHistoricoPorPacienteId(pacienteId);
    }
}