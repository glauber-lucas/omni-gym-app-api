package com.example.omnigym.financeiro;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.example.omnigym.user.User;
import com.example.omnigym.user.UserRepository;
import com.example.omnigym.user.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinanceiroService {

    private final PlanoRepository planoRepository;
    private final FaturaRepository faturaRepository;
    private final UserRepository userRepository;

    public FinanceiroService(PlanoRepository planoRepository, 
                             FaturaRepository faturaRepository, 
                             UserRepository userRepository) {
        this.planoRepository = planoRepository;
        this.faturaRepository = faturaRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Plano cadastrarPlano(PlanoDTO dto) {
        if (planoRepository.existsByNome(dto.nome().trim().toUpperCase())) {
            throw new IllegalArgumentException("Plano já cadastrado com este nome: " + dto.nome());
        }
        Plano plano = new Plano(dto.nome().trim().toUpperCase(), dto.valor(), dto.duracaoMeses());
        return planoRepository.save(plano);
    }

    @Transactional(readOnly = true)
    public List<Plano> listarPlanos() {
        return planoRepository.findAll();
    }

    @Transactional
    public FaturaResponseDTO cadastrarFatura(Long alunoId, FaturaDTO dto) {
        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado com ID: " + alunoId));

        if (aluno.getRole() != Role.ROLE_ALUNO) {
            throw new IllegalArgumentException("Fatura só pode ser gerada para usuários com perfil ALUNO.");
        }

        Plano plano = null;
        BigDecimal valorFatura = dto.valor();

        if (dto.planoId() != null) {
            plano = planoRepository.findById(dto.planoId())
                    .orElseThrow(() -> new IllegalArgumentException("Plano não encontrado com ID: " + dto.planoId()));
            valorFatura = plano.getValor();
        }

        if (valorFatura == null || valorFatura.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da fatura deve ser maior que zero.");
        }

        Date hoje = new Date();
        String status = "PENDENTE";
        if (dto.dataVencimento().before(hoje)) {
            status = "ATRASADO";
        }

        Fatura fatura = new Fatura(aluno, plano, valorFatura, dto.dataVencimento(), status);
        Fatura saved = faturaRepository.save(fatura);

        return mapToResponseDTO(saved);
    }

    @Transactional
    public List<FaturaResponseDTO> listarFaturas(String statusFilter) {
        atualizarStatusFaturasAtrasadas();
        List<Fatura> faturas;
        if (statusFilter != null && !statusFilter.isBlank()) {
            faturas = faturaRepository.findByStatus(statusFilter.trim().toUpperCase());
        } else {
            faturas = faturaRepository.findAll();
        }
        return faturas.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Transactional
    public FaturaResponseDTO registrarPagamento(Long faturaId, BigDecimal valorPago) {
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada com ID: " + faturaId));

        if ("PAGO".equalsIgnoreCase(fatura.getStatus())) {
            throw new IllegalStateException("Esta fatura já foi paga.");
        }

        BigDecimal valorCobrado = fatura.getValor().subtract(fatura.getDesconto());
        if (valorPago == null) {
            valorPago = valorCobrado;
        }

        if (valorPago.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor pago deve ser maior que zero.");
        }

        fatura.setValorPago(valorPago);
        fatura.setDataPagamento(new Date());
        fatura.setStatus("PAGO");

        Fatura saved = faturaRepository.save(fatura);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public FaturaResponseDTO aplicarDesconto(Long faturaId, BigDecimal desconto) {
        Fatura fatura = faturaRepository.findById(faturaId)
                .orElseThrow(() -> new IllegalArgumentException("Fatura não encontrada com ID: " + faturaId));

        if ("PAGO".equalsIgnoreCase(fatura.getStatus())) {
            throw new IllegalStateException("Não é possível aplicar desconto em uma fatura já paga.");
        }

        if (desconto == null || desconto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Desconto inválido.");
        }

        if (desconto.compareTo(fatura.getValor()) > 0) {
            throw new IllegalArgumentException("O desconto não pode ser maior que o valor da fatura.");
        }

        fatura.setDesconto(desconto);
        Fatura saved = faturaRepository.save(fatura);
        return mapToResponseDTO(saved);
    }

    @Transactional
    public RelatorioFaturamentoDTO obterRelatorioFaturamento() {
        atualizarStatusFaturasAtrasadas();
        List<Fatura> faturas = faturaRepository.findAll();

        BigDecimal totalRecebido = BigDecimal.ZERO;
        BigDecimal totalPendente = BigDecimal.ZERO;
        BigDecimal totalAtrasado = BigDecimal.ZERO;

        long pagas = 0;
        long pendentes = 0;
        long atrasadas = 0;

        for (Fatura f : faturas) {
            BigDecimal valorCobrado = f.getValor().subtract(f.getDesconto());
            if ("PAGO".equalsIgnoreCase(f.getStatus())) {
                totalRecebido = totalRecebido.add(f.getValorPago() != null ? f.getValorPago() : valorCobrado);
                pagas++;
            } else if ("PENDENTE".equalsIgnoreCase(f.getStatus())) {
                totalPendente = totalPendente.add(valorCobrado);
                pendentes++;
            } else if ("ATRASADO".equalsIgnoreCase(f.getStatus())) {
                totalAtrasado = totalAtrasado.add(valorCobrado);
                atrasadas++;
            }
        }

        return new RelatorioFaturamentoDTO(
                totalRecebido,
                totalPendente,
                totalAtrasado,
                pagas,
                pendentes,
                atrasadas,
                (long) faturas.size()
        );
    }

    private void atualizarStatusFaturasAtrasadas() {
        Date hoje = new Date();
        List<Fatura> pendentes = faturaRepository.findByStatus("PENDENTE");
        for (Fatura f : pendentes) {
            if (f.getDataVencimento().before(hoje)) {
                f.setStatus("ATRASADO");
                faturaRepository.save(f);
            }
        }
    }

    private FaturaResponseDTO mapToResponseDTO(Fatura fatura) {
        BigDecimal valorCobrado = fatura.getValor().subtract(fatura.getDesconto());
        return new FaturaResponseDTO(
                fatura.getId(),
                fatura.getAluno().getId(),
                fatura.getAluno().getName(),
                fatura.getPlano() != null ? fatura.getPlano().getId() : null,
                fatura.getPlano() != null ? fatura.getPlano().getNome() : "AVULSO",
                fatura.getValor(),
                fatura.getDesconto(),
                valorCobrado,
                fatura.getValorPago(),
                fatura.getDataVencimento(),
                fatura.getDataPagamento(),
                fatura.getStatus()
        );
    }
}
