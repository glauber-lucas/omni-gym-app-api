package com.example.auth.clinico;

import com.example.auth.treino.TreinoExercicio;
import com.example.auth.treino.TreinoExercicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ObservacaoPedagogicaService {

    private final ObservacaoPedagogicaRepository observacaoPedagogicaRepository;
    private final TreinoExercicioRepository treinoExercicioRepository;

    public ObservacaoPedagogicaService(ObservacaoPedagogicaRepository observacaoPedagogicaRepository,
                                        TreinoExercicioRepository treinoExercicioRepository) {
        this.observacaoPedagogicaRepository = observacaoPedagogicaRepository;
        this.treinoExercicioRepository = treinoExercicioRepository;
    }

    @Transactional
    public ObservacaoPedagogicaResponseDTO cadastrarObservacao(Long treinoExercicioId, ObservacaoPedagogicaDTO dto) {
        TreinoExercicio treinoExercicio = treinoExercicioRepository.findById(treinoExercicioId)
                .orElseThrow(() -> new IllegalArgumentException("Exercício do treino não encontrado com ID: " + treinoExercicioId));

        ObservacaoPedagogica observacao = new ObservacaoPedagogica(treinoExercicio, dto.texto());
        ObservacaoPedagogica saved = observacaoPedagogicaRepository.save(observacao);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<ObservacaoPedagogicaResponseDTO> listarPorTreinoExercicio(Long treinoExercicioId) {
        if (!treinoExercicioRepository.existsById(treinoExercicioId)) {
            throw new IllegalArgumentException("Exercício do treino não encontrado com ID: " + treinoExercicioId);
        }

        return observacaoPedagogicaRepository.findByTreinoExercicioId(treinoExercicioId).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ObservacaoPedagogicaResponseDTO mapToResponseDTO(ObservacaoPedagogica obs) {
        return new ObservacaoPedagogicaResponseDTO(
                obs.getId(),
                obs.getTreinoExercicio().getId(),
                obs.getTexto(),
                obs.getDataCriacao()
        );
    }
}
