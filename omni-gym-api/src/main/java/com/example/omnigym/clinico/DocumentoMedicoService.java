package com.example.omnigym.clinico;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.omnigym.user.User;
import com.example.omnigym.user.UserRepository;

@Service
public class DocumentoMedicoService {

    private final DocumentoMedicoRepository documentoRepository;
    private final DocumentoAcessoAuditoriaRepository auditoriaRepository;
    private final UserRepository userRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:52428800}")  // 50MB default
    private Long maxFileSize;

    private static final List<String> TIPOS_MIME_PERMITIDOS = List.of(
        "application/pdf",
        "image/jpeg",
        "image/png",
        "image/tiff",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public DocumentoMedicoService(DocumentoMedicoRepository documentoRepository,
                                  DocumentoAcessoAuditoriaRepository auditoriaRepository,
                                  UserRepository userRepository) {
        this.documentoRepository = documentoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DocumentoMedicoResponseDTO uploadDocumento(Long alunoId, String username,
            DocumentoMedicoUploadDTO dto, MultipartFile arquivo) throws IOException {

        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado com ID: " + alunoId));

        User usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + username));

        // Validações
        validarArquivo(arquivo);

        TipoDocumentoMedico tipo = validarTipo(dto.tipo());

        // Criar diretório se não existir
        criarDiretorioUpload();

        // Salvar arquivo no disco
        String caminhoArquivo = salvarArquivoNoServidor(arquivo, alunoId);

        // Calcular hash do arquivo
        String hash = calcularHashArquivo(arquivo);

        // Criar entidade
        DocumentoMedico documento = new DocumentoMedico(
            aluno,
            tipo,
            dto.descricao(),
            arquivo.getContentType(),
            arquivo.getSize(),
            caminhoArquivo,
            hash,
            dto.dataProximaReavaliacao(),
            usuario
        );

        DocumentoMedico saved = documentoRepository.save(documento);

        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<DocumentoMedicoResponseDTO> listarDocumentos(Long alunoId) {
        User aluno = userRepository.findById(alunoId)
                .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado com ID: " + alunoId));

        return documentoRepository.findByAlunoIdAndAtivoTrue(alunoId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DocumentoMedicoResponseDTO> listarDocumentosPorTipo(Long alunoId, String tipo) {
        TipoDocumentoMedico tipoEnum = validarTipo(tipo);

        return documentoRepository.findByAlunoIdAndTipoOrderByDataUploadDesc(alunoId, tipoEnum)
                .stream()
                .filter(DocumentoMedico::getAtivo)
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<Resource> downloadDocumento(Long documentoId, String username, String ipAddress, String userAgent) {
        DocumentoMedico documento = documentoRepository.findByIdAndAtivoTrue(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado ou inativo com ID: " + documentoId));

        User usuario = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + username));

        // Registrar em auditoria
        DocumentoAcessoAuditoria auditoria = new DocumentoAcessoAuditoria(
            documento, usuario, ipAddress, userAgent
        );
        auditoriaRepository.save(auditoria);

        // Incrementar contador de acessos
        documento.incrementarAcessos();
        documentoRepository.save(documento);

        // Ler arquivo
        Path caminhoCompleto = Paths.get(uploadDir).resolve(documento.getCaminhoArquivo()).normalize();
        File arquivo = caminhoCompleto.toFile();

        if (!arquivo.exists()) {
            throw new IllegalStateException("Arquivo não encontrado no servidor: " + caminhoCompleto);
        }

        Resource resource = new FileSystemResource(arquivo);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(documento.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + arquivo.getName() + "\"")
                .body(resource);
    }

    @Transactional
    public void deletarDocumento(Long documentoId) {
        DocumentoMedico documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado com ID: " + documentoId));

        // Soft delete
        documento.setAtivo(false);
        documentoRepository.save(documento);

        // Opcional: deletar arquivo do servidor
        try {
            Path caminhoCompleto = Paths.get(uploadDir).resolve(documento.getCaminhoArquivo()).normalize();
            Files.deleteIfExists(caminhoCompleto);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar arquivo do servidor", e);
        }
    }

    @Transactional(readOnly = true)
    public List<DocumentoAcessoAuditoria> obterHistoricoAcesso(Long documentoId) {
        DocumentoMedico documento = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new IllegalArgumentException("Documento não encontrado com ID: " + documentoId));

        return auditoriaRepository.findByDocumentoIdOrderByDataAcessoDesc(documentoId);
    }

    // ===== Métodos Auxiliares =====

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new IllegalArgumentException("Arquivo não pode estar vazio");
        }

        if (arquivo.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                String.format("Arquivo excede o tamanho máximo de %d bytes", maxFileSize)
            );
        }

        String contentType = arquivo.getContentType();
        if (!TIPOS_MIME_PERMITIDOS.contains(contentType)) {
            throw new IllegalArgumentException(
                String.format("Tipo de arquivo não permitido: %s", contentType)
            );
        }
    }

    private TipoDocumentoMedico validarTipo(String tipo) {
        try {
            return TipoDocumentoMedico.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de documento inválido: " + tipo);
        }
    }

    private void criarDiretorioUpload() throws IOException {
        Path path = Paths.get(uploadDir);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private String salvarArquivoNoServidor(MultipartFile arquivo, Long alunoId) throws IOException {
        String nomeArquivo = gerarNomeArquivoUnico(arquivo.getOriginalFilename(), alunoId);
        Path caminhoDestino = Paths.get(uploadDir, nomeArquivo);

        Files.createDirectories(caminhoDestino.getParent());
        Files.write(caminhoDestino, arquivo.getBytes());

        return nomeArquivo;
    }

    private String gerarNomeArquivoUnico(String nomeOriginal, Long alunoId) {
        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
        String timestamp = System.currentTimeMillis() + "";
        return String.format("aluno_%d_%s%s", alunoId, timestamp, extensao);
    }

    private String calcularHashArquivo(MultipartFile arquivo) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(arquivo.getBytes());
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao calcular hash do arquivo", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private DocumentoMedicoResponseDTO mapToResponseDTO(DocumentoMedico documento) {
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/documentos/{id}/download")
                .buildAndExpand(documento.getId())
                .toUriString();

        return new DocumentoMedicoResponseDTO(
            documento.getId(),
            documento.getTipo().name(),
            documento.getDescricao(),
            documento.getMimeType(),
            documento.getTamanhoBytes(),
            documento.getDataUpload(),
            documento.getDataProximaReavaliacao(),
            documento.getCriadoPor().getName(),
            documento.getAcessosCount(),
            documento.getAtivo(),
            downloadUrl
        );
    }

    public boolean isAlunoDono(Long documentoId, String username) {
        DocumentoMedico documento = documentoRepository.findById(documentoId)
                .orElse(null);

        if (documento == null) {
            return false;
        }

        User usuario = userRepository.findByUsername(username).orElse(null);
        if (usuario == null) return false;
        return documento.getAluno().getId().equals(usuario.getId());
    }

    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + username))
                .getId();
    }
}
