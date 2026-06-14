package com.example.omnigym.exercicio;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeedRunner implements CommandLineRunner {

    private final ArticulacaoRepository articulacaoRepository;
    private final AcessorioRepository acessorioRepository;

    public DataSeedRunner(ArticulacaoRepository articulacaoRepository,
                          AcessorioRepository acessorioRepository) {
        this.articulacaoRepository = articulacaoRepository;
        this.acessorioRepository = acessorioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        seedArticulacoes();
        seedAcessorios();
    }

    private void seedArticulacoes() {
        List<String> nomesArticulacoes = List.of(
                "OMBRO", "COTOVELO", "PUNHO", "JOELHO", "QUADRIL", 
                "TORNOZELO", "COLUNA_CERVICAL", "COLUNA_TORACICA", 
                "COLUNA_LOMBAR", "ESCAPULA", "PUNHO_MAO", "TORNOZELO_PE"
        );

        for (String nome : nomesArticulacoes) {
            if (!articulacaoRepository.existsByNome(nome)) {
                articulacaoRepository.save(new Articulacao(nome));
                System.out.println("Articulacao cadastrada no seed: " + nome);
            }
        }
    }

    private void seedAcessorios() {
        List<String> nomesAcessorios = List.of(
                "STRAP", "HOOK", "CINTO_LOMBAR", "PUXADOR_ADAPTADO", 
                "ENCOSTO_ELEVADO", "ALCA_TORNOZELO", "ESTRAP_ABDOMINAL", 
                "ADAPTADOR_MAO", "FAIXA_ELASTICA", "CUNHA_CALCANHAR", 
                "ALMOFADA_APOIO", "PULSEIRA_PESO", "SUPORTE_TRONCO", 
                "PEGADA_EMBARRADA"
        );

        for (String nome : nomesAcessorios) {
            if (!acessorioRepository.existsByNome(nome)) {
                acessorioRepository.save(new Acessorio(nome));
                System.out.println("Acessorio cadastrado no seed: " + nome);
            }
        }
    }
}
