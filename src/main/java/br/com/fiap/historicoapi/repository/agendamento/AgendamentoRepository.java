package br.com.fiap.historicoapi.repository.agendamento;

import br.com.fiap.historicoapi.model.entity.agendamento.Agendamento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    @EntityGraph(attributePaths = {"paciente", "medico"})
    List<Agendamento> findByPacienteId(Integer pacienteId);

}