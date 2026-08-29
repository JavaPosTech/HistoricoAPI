package br.com.fiap.historicoapi.repository.historicopaciente;

import br.com.fiap.historicoapi.model.entity.historicopaciente.HistoricoPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricoPacienteRepository extends JpaRepository<HistoricoPaciente, Integer> {

    List<HistoricoPaciente> findByPacienteId(Integer pacienteId);

}
