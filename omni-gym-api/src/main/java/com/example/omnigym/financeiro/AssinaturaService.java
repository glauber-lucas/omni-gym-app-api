package com.example.omnigym.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.omnigym.user.User;
import com.example.omnigym.user.UserRepository;

@Service
public class AssinaturaService {

    private final AssinaturaRepository assinaturaRepository;
    private final PlanoRepository planoRepository;
    private final FaturaRepository faturaRepository;
    private final UserRepository userRepository;

    public AssinaturaService(AssinaturaRepository assinaturaRepository,
                            PlanoRepository planoRepository,
                            FaturaRepository faturaRepository,
                            UserRepository userRepository) {
        this.assinaturaRepository = assinaturaRepository;
        this.planoRepository = planoRepository;
        this.faturaRepository = faturaRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AssinaturaResponseDTO criarAssinaturaComFaturas(Long alunoId, Long planoId) {
        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado com ID: " + alunoId));

        Plano plano = planoRepository.findById(planoId)
                .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado com ID: " + planoId));

        Date dataInicio = new Date();
        Date dataFim = calcularDataFim(dataInicio, plano.getDuracaoMeses());

        Assinatura assinatura = new Assinatura(aluno, plano, dataInicio, dataFim);
        Assinatura saved = assinaturaRepository.save(assinatura);

        List<Fatura> faturas = gerarFaturasAutomaticas(saved);
        saved.setFaturasGeradas(faturas.size());
        assinaturaRepository.save(saved);

        return mapToDTO(saved, faturas);
    }

    @Transactional(readOnly = true)
    public AssinaturaResponseDTO obterAssinaturaAtiva(Long alunoId) {
        Assinatura assinatura = assinaturaRepository
                .findByAlunoIdAndStatusOrderByDataInicioDesc(alunoId, StatusAssinatura.ATIVA)
                .orElseThrow(() -> new IllegalArgumentException("Nenhuma assinatura ativa encontrada para o aluno: " + alunoId));

        List<Fatura> faturas = faturaRepository.findByAlunoIdAndPlanoId(alunoId, assinatura.getPlano().getId());
        return mapToDTO(assinatura, faturas);
    }

    @Transactional(readOnly = true)
    public List<AssinaturaResponseDTO> listarAssinaturasDoAluno(Long alunoId) {
        List<Assinatura> assinaturas = assinaturaRepository.findByAlunoId(alunoId);
        return assinaturas.stream()
                .map(a -> {
                    List<Fatura> faturas = faturaRepository.findByAlunoIdAndPlanoId(alunoId, a.getPlano().getId());
                    return mapToDTO(a, faturas);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelarAssinatura(Long assinaturaId) {
        Assinatura assinatura = assinaturaRepository.findById(assinaturaId)
                .orElseThrow(() -> new IllegalArgumentException("Assinatura não encontrada com ID: " + assinaturaId));

        assinatura.setStatus(StatusAssinatura.CANCELADA);
        assinaturaRepository.save(assinatura);
    }

    private List<Fatura> gerarFaturasAutomaticas(Assinatura assinatura) {
        List<Fatura> faturas = new ArrayList<>();
        Plano plano = assinatura.getPlano();

        for (int i = 0; i < plano.getDuracaoMeses(); i++) {
            Date dataVencimento = adicionarMeses(assinatura.getDataInicio(), i + 1);

            Fatura fatura = new Fatura(
                assinatura.getAluno(),
                plano,
                plano.getValor(),
                dataVencimento,
                "PENDENTE"
            );

            Fatura saved = faturaRepository.save(fatura);
            faturas.add(saved);
        }

        return faturas;
    }

    private Date adicionarMeses(Date data, int meses) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        cal.add(Calendar.MONTH, meses);
        return cal.getTime();
    }

    private Date calcularDataFim(Date dataInicio, Integer duracaoMeses) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dataInicio);
        cal.add(Calendar.MONTH, duracaoMeses);
        return cal.getTime();
    }

    private AssinaturaResponseDTO mapToDTO(Assinatura assinatura, List<Fatura> faturas) {
        List<FaturaResponseDTO> faturasDTO = faturas.stream()
                .map(f -> {
                    BigDecimal valorCobrado = f.getValor().subtract(f.getDesconto());
                    return new FaturaResponseDTO(
                        f.getId(),
                        f.getAluno().getId(),
                        f.getAluno().getName(),
                        f.getPlano() != null ? f.getPlano().getId() : null,
                        f.getPlano() != null ? f.getPlano().getNome() : "AVULSO",
                        f.getValor(),
                        f.getDesconto(),
                        valorCobrado,
                        f.getValorPago(),
                        f.getDataVencimento(),
                        f.getDataPagamento(),
                        f.getStatus()
                    );
                })
                .collect(Collectors.toList());

        return new AssinaturaResponseDTO(
            assinatura.getId(),
            assinatura.getAluno().getId(),
            assinatura.getAluno().getName(),
            assinatura.getPlano().getId(),
            assinatura.getPlano().getNome(),
            assinatura.getPlano().getDuracaoMeses(),
            assinatura.getDataInicio(),
            assinatura.getDataFim(),
            assinatura.getStatus().name(),
            assinatura.getFaturasGeradas(),
            faturasDTO
        );
    }
}
