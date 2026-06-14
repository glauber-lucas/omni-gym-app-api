# 🏋️ Omni Gym API

Backend do ecossistema digital **Omni Gym**, focado em acessibilidade biomecânica e otimização logística em academias.

---

## 🛠️ Stack Tecnológica

* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.1.6
* **Segurança:** Spring Security + JWT (JSON Web Tokens)
* **Persistência:** Spring Data JPA + Hibernate
* **Banco de Dados:** PostgreSQL (criação automática do banco `omni_gym` na inicialização do backend)
* **Gerenciador de Dependências:** Maven

---

## 🏛️ Arquitetura por Features (Vertical Slices)

* **`com.example.omnigym.core`**: Configurações transversais (Segurança JWT, tratamento global de exceções).
* **`com.example.omnigym.user`**: Controle de usuários, segurança baseada em papéis (`ROLE_ALUNO` e `ROLE_INSTRUTOR`) e fluxo de autenticação.
* **`com.example.omnigym.matricula`**: Cadastro de matrículas, homologação de alunos e mapeamento de perfis biomecânicos (limitações articulares e estabilidade de tronco).
* **`com.example.omnigym.exercicio`**: Catálogo global de exercícios, exigências articulares, máquinas/estações de trabalho, acessórios assistivos e cadastro dinâmico.
* **`com.example.omnigym.treino`**: Gerenciamento de fichas de treino, Motor de Acessibilidade (filtragem de segurança) e Otimizador Logístico (Modo Estação Única).
* **`com.example.omnigym.clinico`**: Módulo Clínico para upload de dossiês médicos e orientações pedagógicas em tempo real.
* **`com.example.omnigym.financeiro`**: Módulo Financeiro para controle de faturamento, planos, faturas, descontos e relatórios.

---

## 🚀 Endpoints da API

### 🔐 Autenticação (`/auth`)
| Método | Endpoint | Perfil Necessário | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/auth/register` | Público | Registra um novo usuário (Aluno ou Instrutor) |
| **POST** | `/auth/local` | Público | Autentica um usuário e retorna o token JWT e dados do perfil |
| **POST** | `/auth/login` | Público | Autenticação padrão alternativa |
| **POST** | `/auth/refresh-token` | Público | Atualiza um token JWT expirado usando o Refresh Token |
| **GET** | `/auth/me` | Autenticado | Retorna os detalhes básicos do usuário logado |

### 📝 Matrículas & Biomecânica (`/aluno` e `/instrutor`)
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/aluno/matricula` | `ROLE_ALUNO` | Aluno preenche sua própria ficha de matrícula |
| **GET** | `/aluno/matricula` | `ROLE_ALUNO` | Obtém os dados de matrícula do próprio aluno logado |
| **GET** | `/instrutor/matriculas` | `ROLE_INSTRUTOR` | Lista todas as matrículas cadastradas e seus status |
| **GET** | `/instrutor/matriculas/pendentes` | `ROLE_INSTRUTOR` | Lista alunos com matrícula aguardando homologação |
| **GET** | `/instrutor/matriculas/{alunoId}` | `ROLE_INSTRUTOR` | Exibe os detalhes cadastrais e biomecânicos de um aluno |
| **POST** | `/instrutor/matriculas/{alunoId}/homologar` | `ROLE_INSTRUTOR` | Homologa a matrícula do aluno selecionado |
| **POST** | `/instrutor/alunos/{alunoId}/perfil-biomecanico` | `ROLE_INSTRUTOR` | Mapeia estabilidade de tronco e restrições articulares do aluno |

### 🏋️ Catálogo de Exercícios, Articulações & Acessórios
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/exercicios` | `ROLE_INSTRUTOR` | Cadastra um novo exercício no catálogo global |
| **GET** | `/exercicios` | Qualquer Perfil | Lista todos os exercícios cadastrados no sistema |
| **POST** | `/articulacoes` | `ROLE_INSTRUTOR` | Cadastra uma nova articulação biomecânica |
| **GET** | `/articulacoes` | Qualquer Perfil | Lista todas as articulações biomecânicas |
| **POST** | `/acessorios` | `ROLE_INSTRUTOR` | Cadastra um novo acessório assistivo de adaptação |
| **GET** | `/acessorios` | Qualquer Perfil | Lista todos os acessórios assistivos cadastrados |

### 📅 Fichas de Treino (`/treinos` e `/treino-diario`)
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/instrutor/alunos/{alunoId}/treinos` | `ROLE_INSTRUTOR` | Cria e vincula uma ficha de treino para o aluno |
| **GET** | `/aluno/treino-diario` | `ROLE_ALUNO` | Retorna o treino do dia adaptado e reordenado em tempo real |

### 🏥 Clínico & Observações Pedagógicas (Módulo Clínico)
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/instrutor/alunos/{alunoId}/dossie-clinico` | `ROLE_INSTRUTOR` | Cadastra exames, laudos e reavaliações médicas do aluno |
| **POST** | `/instrutor/treinos/{treinoExercicioId}/observacoes` | `ROLE_INSTRUTOR` | Adiciona orientações pedagógicas para a execução de um exercício da ficha |

### 💳 Módulo Financeiro (`/instrutor/financeiro`)
| Método | Endpoint | Perfil | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/instrutor/financeiro/planos` | `ROLE_INSTRUTOR` | Cadastra planos de mensalidade da academia |
| **GET** | `/instrutor/financeiro/planos` | `ROLE_INSTRUTOR` | Lista os planos de mensalidade cadastrados |
| **POST** | `/instrutor/financeiro/alunos/{alunoId}/faturas` | `ROLE_INSTRUTOR` | Vincula um plano ao aluno ou gera fatura manual |
| **GET** | `/instrutor/financeiro/faturas` | `ROLE_INSTRUTOR` | Lista as faturas geradas (permite filtro `?status=`) |
| **POST** | `/instrutor/financeiro/faturas/{faturaId}/desconto` | `ROLE_INSTRUTOR` | Aplica descontos nas faturas pendentes do aluno |
| **POST** | `/instrutor/financeiro/faturas/{faturaId}/pagar` | `ROLE_INSTRUTOR` | Registra pagamento manual de fatura |
| **GET** | `/instrutor/financeiro/relatorio-faturamento` | `ROLE_INSTRUTOR` | Emite relatório consolidado de faturamento e recebimentos |

---

## 💻 Como Executar Localmente

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

### 3. Rodando o Projeto
Abra o terminal na pasta raiz do projeto backend (`omni-gym-api`) e execute:

```bash
mvn spring-boot:run
```

Na primeira inicialização, a aplicação irá:
1. Criar o schema das tabelas.
2. Executar o **`DataSeedRunner`** para preencher automaticamente as tabelas de `Articulacao` e `Acessorio` com as opções padrões.

### 4. Executando a Suíte de Testes
Para validar a integridade de todas as regras de negócio, execute:

```bash
mvn clean test
```
