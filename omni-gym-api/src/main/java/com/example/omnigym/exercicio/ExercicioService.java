package com.example.omnigym.exercicio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.omnigym.matricula.EstabilidadeTronco;

@Service
public class ExercicioService {

    private final ExercicioRepository exercicioRepository;
    private final ExercicioAdaptacaoRepository adaptacaoRepository;
    private final ArticulacaoRepository articulacaoRepository;
    private final AcessorioRepository acessorioRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public ExercicioService(ExercicioRepository exercicioRepository,
                            ExercicioAdaptacaoRepository adaptacaoRepository,
                            ArticulacaoRepository articulacaoRepository,
                            AcessorioRepository acessorioRepository) {
        this.exercicioRepository = exercicioRepository;
        this.adaptacaoRepository = adaptacaoRepository;
        this.articulacaoRepository = articulacaoRepository;
        this.acessorioRepository = acessorioRepository;
    }

    @Transactional
    public ExercicioResponseDTO cadastrarExercicio(ExercicioDTO dto) {
        if (dto.exigenciasIds() == null || dto.exigenciasIds().isEmpty()) {
            throw new IllegalArgumentException("O exercício deve possuir ao menos uma articulação exigida.");
        }

        EstabilidadeTronco estabilidade;
        try {
            estabilidade = EstabilidadeTronco.valueOf(dto.estabilidadeTroncoMinima().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Estabilidade mínima de tronco inválida: " + dto.estabilidadeTroncoMinima());
        }

        Set<Articulacao> exigencias = new HashSet<>();
        for (Long id : dto.exigenciasIds()) {
            Articulacao art = articulacaoRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Articulação exigida não encontrada com ID: " + id));
            exigencias.add(art);
        }

        Exercicio exercicio = new Exercicio();
        exercicio.setNome(dto.nome());
        exercicio.setGrupoMuscular(dto.grupoMuscular());
        exercicio.setEstacaoTrabalho(dto.estacaoTrabalho());
        exercicio.setEstabilidadeTroncoMinima(estabilidade);
        exercicio.setExigencias(exigencias);
        exercicio.setImagemUrl(dto.imagemUrl());
 
        Exercicio savedExercicio = exercicioRepository.save(exercicio);

        List<ExercicioAdaptacao> savedAdaptacoes = new ArrayList<>();
        if (dto.adaptacoes() != null && !dto.adaptacoes().isEmpty()) {
            for (ExercicioAdaptacaoDTO adaptDTO : dto.adaptacoes()) {
                Articulacao art = articulacaoRepository.findById(adaptDTO.articulacaoId())
                        .orElseThrow(() -> new IllegalArgumentException("Articulação de adaptação não encontrada com ID: " + adaptDTO.articulacaoId()));

                Acessorio acessorio = acessorioRepository.findById(adaptDTO.acessorioId())
                        .orElseThrow(() -> new IllegalArgumentException("Acessório não encontrado com ID: " + adaptDTO.acessorioId()));

                ExercicioAdaptacao adaptacao = new ExercicioAdaptacao();
                adaptacao.setExercicio(savedExercicio);
                adaptacao.setArticulacao(art);
                adaptacao.setAcessorio(acessorio);
                adaptacao.setInstrucaoTexto(adaptDTO.instrucaoTexto());

                savedAdaptacoes.add(adaptacaoRepository.save(adaptacao));
            }
        }

        return mapToResponseDTO(savedExercicio, savedAdaptacoes);
    }

    @Transactional
    public ExercicioResponseDTO cadastrarExercicioComImagem(ExercicioDTO dto, MultipartFile imagem) throws IOException {
        ExercicioResponseDTO response = cadastrarExercicio(dto);
        if (imagem != null && !imagem.isEmpty()) {
            return uploadImagem(response.id(), imagem);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<ExercicioResponseDTO> listarExercicios() {
        return exercicioRepository.findAll().stream()
                .map(ex -> {
                    List<ExercicioAdaptacao> adaps = adaptacaoRepository.findByExercicioId(ex.getId());
                    return mapToResponseDTO(ex, adaps);
                })
                .collect(Collectors.toList());
    }

    private ExercicioResponseDTO mapToResponseDTO(Exercicio exercicio, List<ExercicioAdaptacao> adaptacoes) {
        List<String> exigencias = exercicio.getExigencias().stream()
                .map(Articulacao::getNome)
                .collect(Collectors.toList());

        List<ExercicioResponseDTO.AdaptacaoDetail> adaptDetails = adaptacoes.stream()
                .map(ad -> new ExercicioResponseDTO.AdaptacaoDetail(
                        ad.getId(),
                        ad.getArticulacao().getNome(),
                        ad.getAcessorio().getNome(),
                        ad.getInstrucaoTexto()
                ))
                .collect(Collectors.toList());

        return new ExercicioResponseDTO(
                exercicio.getId(),
                exercicio.getNome(),
                exercicio.getGrupoMuscular(),
                exercicio.getEstacaoTrabalho(),
                exercicio.getEstabilidadeTroncoMinima() != null ? exercicio.getEstabilidadeTroncoMinima().name() : null,
                exigencias,
                adaptDetails,
                exercicio.getImagemUrl()
        );
    }

    @Transactional
    public Articulacao cadastrarArticulacao(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da articulacao nao pode ser vazio.");
        }
        String nomeUpper = nome.trim().toUpperCase();
        if (articulacaoRepository.existsByNome(nomeUpper)) {
            throw new IllegalArgumentException("Articulacao ja cadastrada: " + nomeUpper);
        }
        return articulacaoRepository.save(new Articulacao(nomeUpper));
    }

    @Transactional(readOnly = true)
    public List<Articulacao> listarArticulacoes() {
        return articulacaoRepository.findAll();
    }

    @Transactional
    public Acessorio cadastrarAcessorio(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do acessorio nao pode ser vazio.");
        }
        String nomeUpper = nome.trim().toUpperCase();
        if (acessorioRepository.existsByNome(nomeUpper)) {
            throw new IllegalArgumentException("Acessorio ja cadastrado: " + nomeUpper);
        }
        return acessorioRepository.save(new Acessorio(nomeUpper));
    }

    @Transactional(readOnly = true)
    public List<Acessorio> listarAcessorios() {
        return acessorioRepository.findAll();
    }

    @Transactional
    public ExercicioResponseDTO uploadImagem(Long id, MultipartFile imagem) throws IOException {
        Exercicio exercicio = exercicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado com ID: " + id));

        if (imagem == null || imagem.isEmpty()) {
            throw new IllegalArgumentException("A imagem não pode estar vazia.");
        }

        String contentType = imagem.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("O arquivo deve ser uma imagem válida (JPEG, PNG, etc.).");
        }

        // Criar diretório se não existir
        Path dirPath = Paths.get(uploadDir, "exercicios");
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Salvar imagem no disco
        String extensao = "";
        String originalFilename = imagem.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            extensao = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String nomeArquivo = "exercicio_" + id + "_" + System.currentTimeMillis() + extensao;
        Path caminhoDestino = dirPath.resolve(nomeArquivo);

        Files.write(caminhoDestino, imagem.getBytes());

        // Atualizar URL e caminho
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/exercicios/{id}/imagem")
                .buildAndExpand(id)
                .toUriString();

        exercicio.setImagemUrl(downloadUrl);
        exercicio.setImagemCaminho(caminhoDestino.toString());
        Exercicio saved = exercicioRepository.save(exercicio);

        List<ExercicioAdaptacao> adaps = adaptacaoRepository.findByExercicioId(id);
        return mapToResponseDTO(saved, adaps);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<Resource> obterImagem(Long id) {
        Exercicio exercicio = exercicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado com ID: " + id));

        String caminho = exercicio.getImagemCaminho();
        if (caminho == null || caminho.isBlank()) {
            throw new IllegalArgumentException("Exercício não possui imagem cadastrada.");
        }

        Path caminhoCompleto = Paths.get(caminho);
        java.io.File arquivo = caminhoCompleto.toFile();

        if (!arquivo.exists()) {
            throw new IllegalStateException("Arquivo da imagem não encontrado no servidor.");
        }

        Resource resource = new FileSystemResource(arquivo);
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(caminhoCompleto);
        } catch (IOException e) {
            // ignorar
        }
        if (mimeType == null) {
            mimeType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + arquivo.getName() + "\"")
                .body(resource);
    }
}
