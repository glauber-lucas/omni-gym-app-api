# Omni Gym API

Backend do ecossistema digital **Omni Gym**, focado em acessibilidade biomecânica e otimização logística em academias.

---

## Stack

* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.1.6
* **Segurança:** Spring Security + JWT (JSON Web Tokens)
* **Persistência:** Spring Data JPA + Hibernate
* **Banco de Dados:** PostgreSQL (criação automática do banco `omni_gym` na inicialização do backend)
* **Gerenciador de Dependências:** Maven
* **Containerização:** Docker + Docker Compose (Multi-stage Build)

---

## Arquitetura por Features

* **`com.example.omnigym.core`**: Configurações transversais (Segurança JWT, tratamento global de exceções formatado por campo para o Frontend).
* **`com.example.omnigym.user`**: Controle de usuários, segurança baseada em papéis (`ROLE_ALUNO` e `ROLE_INSTRUTOR`), fluxo de autenticação e proteção contra escalada de privilégios.
* **`com.example.omnigym.matricula`**: Cadastro de matrículas, homologação de alunos e mapeamento de perfis biomecânicos (limitações articulares e estabilidade de tronco).
* **`com.example.omnigym.exercicio`**: Catálogo global de exercícios, exigências articulares, máquinas/estações de trabalho, acessórios assistivos e cadastro dinâmico.
* **`com.example.omnigym.treino`**: Gerenciamento de fichas de treino, Motor de Acessibilidade (filtragem de segurança), Otimizador Logístico (Modo Estação Única) e edição de treinos de forma segura pelo próprio aluno.
* **`com.example.omnigym.clinico`**: Módulo Clínico para upload de dossiês médicos, documentos médicos com auditoria real de acesso, e orientações pedagógicas em tempo real.
* **`com.example.omnigym.financeiro`**: Módulo Financeiro para controle de faturamento, planos, faturas, assinaturas, gateway de pagamento, descontos, relatórios e painel self-service do aluno.

---

## Endpoints da API

### Autenticação (`/auth`)
| Método | Endpoint | Perfil Necessário | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/auth/register` | Público | Registra um novo usuário (Alunos são livres; Instrutores exigem chave secreta) |
| **POST** | `/auth/local` | Público | Autentica um usuário e retorna o token JWT e dados do perfil |
| **POST** | `/auth/login` | Público | Autenticação padrão alternativa |
| **POST** | `/auth/refresh-token` | Público | Renova o token de acesso usando um Refresh Token de 7 dias (claim `type="refresh"`) |
| **GET** | `/auth/me` | Autenticado | Retorna os detalhes básicos do usuário logado |

### Matrículas & Biomecânica (`/aluno` e `/instrutor`)
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/aluno/matricula` | `ROLE_ALUNO` | Aluno preenche sua própria ficha de matrícula |
| **GET** | `/aluno/matricula` | `ROLE_ALUNO` | Obtém os dados de matrícula do próprio aluno logado |
| **GET** | `/instrutor/matriculas` | `ROLE_INSTRUTOR` | Lista todas as matrículas cadastradas e seus status |
| **GET** | `/instrutor/matriculas/pendentes` | `ROLE_INSTRUTOR` | Lista alunos com matrícula aguardando homologação |
| **GET** | `/instrutor/matriculas/{alunoId}` | `ROLE_INSTRUTOR` | Exibe os detalhes cadastrais (respostas do formulário preenchido pelo aluno) e biomecânicos de um aluno |
| **POST** | `/instrutor/matriculas/{alunoId}/homologar` | `ROLE_INSTRUTOR` | Homologa a matrícula do aluno selecionado |
| **POST** | `/instrutor/matriculas/{alunoId}/homologar-com-plano` | `ROLE_INSTRUTOR` | Homologa a matrícula e vincula um plano de assinatura simultaneamente |
| **POST** | `/instrutor/alunos/{alunoId}/perfil-biomecanico` | `ROLE_INSTRUTOR` | Mapeia estabilidade de tronco e restrições articulares do aluno |
| **GET** | `/instrutor/alunos/{alunoId}/perfil-biomecanico/historico` | `ROLE_INSTRUTOR` | Lista o histórico de alterações do perfil biomecânico do aluno |

### Catálogo de Exercícios, Articulações & Acessórios
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/exercicios` | `ROLE_INSTRUTOR` | Cadastra um novo exercício (suporta JSON puro ou `multipart/form-data` para upload direto de imagem) |
| **GET** | `/exercicios` | Qualquer Perfil | Lista todos os exercícios cadastrados no sistema |
| **POST** | `/exercicios/{id}/imagem` | `ROLE_INSTRUTOR` | Faz upload de uma imagem representativa para um exercício existente |
| **GET** | `/exercicios/{id}/imagem` | Qualquer Perfil | Retorna o arquivo da imagem do exercício para visualização |
| **POST** | `/articulacoes` | `ROLE_INSTRUTOR` | Cadastra uma nova articulação biomecânica |
| **GET** | `/articulacoes` | Qualquer Perfil | Lista todas as articulações biomecânicas |
| **POST** | `/acessorios` | `ROLE_INSTRUTOR` | Cadastra um novo acessório assistivo de adaptação |
| **GET** | `/acessorios` | Qualquer Perfil | Lista todos os acessórios assistivos cadastrados |

> [!TIP]
> O endpoint `POST /exercicios` aceita tanto JSON puro quanto `multipart/form-data`. Para realizar o cadastro e o upload da imagem em uma única chamada:
> - **Content-Type**: `multipart/form-data`
> - **Parte `exercicio`** (Content-Type: `application/json`): JSON contendo os dados do exercício (`ExercicioDTO`)
> - **Parte `imagem`** (Opcional, arquivo): O arquivo de imagem (JPEG, PNG, etc.) a ser cadastrado para o exercício


### Fichas de Treino (`/treinos` e `/treino-diario`)
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/instrutor/alunos/{alunoId}/treinos` | `ROLE_INSTRUTOR` | Cria e vincula uma ficha de treino para o aluno |
| **GET** | `/aluno/treino-diario` | `ROLE_ALUNO` | Retorna o treino do dia adaptado e reordenado em tempo real |
| **GET** | `/aluno/treino/exercicios-disponiveis` | `ROLE_ALUNO` | Lista exercícios disponíveis para o aluno (exclui bloqueados biomecanicamente) |
| **PUT** | `/aluno/treino/editar` | `ROLE_ALUNO` | Edita a ficha de treino ativa do aluno (com validação de segurança via motor) |

### Clínico & Observações Pedagógicas (Módulo Clínico)
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/instrutor/alunos/{alunoId}/dossie-clinico` | `ROLE_INSTRUTOR` | Cadastra exames, laudos e reavaliações médicas do aluno |
| **POST** | `/instrutor/treinos/{treinoExercicioId}/observacoes` | `ROLE_INSTRUTOR` | Adiciona orientações pedagógicas para a execução de um exercício da ficha |

### Documentos Médicos (Upload, Download Seguro & Auditoria)
*Nota: Todos os endpoints de posse do aluno validam dinamicamente se o ID/Username requisitante é o verdadeiro proprietário (Proteção contra IDOR).*
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/aluno/documentos-medicos/upload` | `ROLE_ALUNO` | Upload de documento médico com resolução dinâmica do ID do Aluno logado |
| **GET** | `/instrutor/alunos/{alunoId}/documentos-medicos` | `ROLE_INSTRUTOR` | Lista todos os documentos médicos de um aluno |
| **GET** | `/instrutor/alunos/{alunoId}/documentos-medicos/tipo` | `ROLE_INSTRUTOR` | Filtra documentos médicos por tipo (`?tipo=LAUDO_MEDICO`) |
| **GET** | `/api/documentos/{documentoId}/download` | `ROLE_INSTRUTOR` / `ROLE_ALUNO` | Download seguro com checagem de propriedade e registro real no log de auditoria |
| **DELETE** | `/documentos/{documentoId}` | `ROLE_INSTRUTOR` / `ROLE_ALUNO` | Soft delete validado de documento médico |
| **GET** | `/instrutor/documentos/{documentoId}/historico-acesso` | `ROLE_INSTRUTOR` | Histórico de acessos ao documento (auditoria completa por username real) |

### Módulo Financeiro Administrativo (`/instrutor/financeiro`)
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/instrutor/financeiro/planos` | `ROLE_INSTRUTOR` | Cadastra planos de mensalidade da academia |
| **GET** | `/instrutor/financeiro/planos` | `ROLE_INSTRUTOR` | Lista os planos de mensalidade cadastrados |
| **POST** | `/instrutor/financeiro/alunos/{alunoId}/faturas` | `ROLE_INSTRUTOR` | Vincula um plano ao aluno ou gera fatura manual |
| **GET** | `/instrutor/financeiro/faturas` | `ROLE_INSTRUTOR` | Lista as faturas geradas (permite filtro `?status=`) |
| **POST** | `/instrutor/financeiro/faturas/{faturaId}/desconto` | `ROLE_INSTRUTOR` | Aplica descontos nas faturas pendentes do aluno |
| **POST** | `/instrutor/financeiro/faturas/{faturaId}/pagar` | `ROLE_INSTRUTOR` | Registra pagamento manual de fatura |
| **GET** | `/instrutor/financeiro/relatorio-faturamento` | `ROLE_INSTRUTOR` | Emite relatório consolidado de faturamento e recebimentos |

### Assinaturas (`/instrutor/financeiro`)
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/instrutor/financeiro/alunos/{alunoId}/assinatura` | `ROLE_INSTRUTOR` | Cria assinatura com faturas automáticas para o aluno |
| **GET** | `/instrutor/financeiro/alunos/{alunoId}/assinatura` | `ROLE_INSTRUTOR` | Obtém a assinatura ativa do aluno |
| **GET** | `/instrutor/financeiro/alunos/{alunoId}/assinaturas` | `ROLE_INSTRUTOR` | Lista todas as assinaturas (ativas e canceladas) do aluno |
| **DELETE** | `/instrutor/financeiro/assinatura/{assinaturaId}/cancelar` | `ROLE_INSTRUTOR` | Cancela uma assinatura do aluno |

### Gateway de Pagamento & Financeiro Aluno (Self-Service)
*Nota: Endpoints protegidos por validação de propriedade de fatura para mitigar fraudes inter-aluno.*
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **GET** | `/aluno/financeiro/faturas` | `ROLE_ALUNO` | Consulta de faturas emitidas para o próprio aluno logado |
| **POST** | `/aluno/financeiro/faturas/{faturaId}/checkout` | `ROLE_ALUNO` | Inicia checkout de pagamento via gateway de uma fatura própria |
| **POST** | `/aluno/financeiro/pagamentos/{pagamentoId}/simular-confirmar` | `ROLE_ALUNO` | Simula confirmação de sucesso pelo gateway de pagamento |
| **POST** | `/aluno/financeiro/pagamentos/{pagamentoId}/simular-recusar` | `ROLE_ALUNO` | Simula resposta de recusa/falha pelo gateway de pagamento |

---

## Como Executar Localmente

### 1. Pré-requisitos
* **Java SDK 17 ou superior** instalado e configurado no PATH
* **Maven** instalado e configurado no PATH
* **PostgreSQL** instalado e executando na porta padrão (`5432`)

### 2. Configuração do Banco de Dados
A aplicação possui um inicializador automático (`OmniGymApplication.java`) que verifica a existência do banco de dados `omni_gym` e o cria de forma transparente caso ainda não exista no servidor local do PostgreSQL.

Certifique-se de configurar as credenciais do seu PostgreSQL no arquivo `src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/omni_gym
    username: postgres
    password: 123 # Insira sua senha do banco aqui
```

### 3. Configuração de Upload de Arquivos
O upload de documentos médicos é configurável via `application.yml`:
```yaml
server:
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB

app:
  upload:
    dir: uploads            # Diretório onde os arquivos são salvos
    max-file-size: 52428800 # 50MB em bytes
```

### 4. Rodando o Projeto

#### Opção A: Usando Docker
Se você possui o Docker instalado, basta abrir o terminal na pasta raiz do repositório (onde está o arquivo `docker-compose.yml`) e executar:

```bash
docker compose up --build
```
Isso irá iniciar o contêiner do banco de dados PostgreSQL e compilar/executar a API automaticamente na porta `8080`. O banco é criado e populado por completo no primeiro boot.

#### Opção B: Execução Nativa
Abra o terminal na pasta raiz do projeto backend (`omni-gym-api`) e execute:

```bash
mvn spring-boot:run
```

Na primeira inicialização, a aplicação irá:
1. Criar o schema das tabelas.
2. Executar o **`DataSeedRunner`** para preencher automaticamente as tabelas de `Articulacao` e `Acessorio` com as opções padrões.

### 5. Executando a Suíte de Testes
Para validar a integridade de todas as regras de negócio, execute:

```bash
mvn clean test
```
