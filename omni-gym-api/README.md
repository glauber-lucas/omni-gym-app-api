# Omni Gym API

Este é o backend unificado para o ecossistema **Omni Gym** (servindo tanto o aplicativo do Aluno quanto o do Instrutor). O projeto gerencia autenticação baseada em perfis, homologação de matrículas, perfis biomecânicos e de saúde dos alunos, além de possuir um **Motor de Acessibilidade** e um **Otimizador Logístico** que adaptam e ordenam os exercícios da ficha de treino diária do aluno em tempo real.

---

## 🛠️ Stack Tecnológica

* **Linguagem:** Java 17
* **Framework:** Spring Boot 3.1.6
* **Segurança:** Spring Security + JWT (JSON Web Tokens)
* **Persistência:** Spring Data JPA + Hibernate
* **Banco de Dados:** PostgreSQL (criação automática do banco `omni_gym` na inicialização do backend)
* **Gerenciador de Dependências:** Maven

---

## 📦 Estrutura de Pacotes (Arquitetura por Features)

O backend é organizado usando a abordagem de **Vertical Slices / Features**, promovendo coesão e facilidade de manutenção:

* **`com.example.auth.core`**: Configurações transversais (Segurança JWT, tratamento global de erros/exceções).
* **`com.example.auth.user`**: Controle de usuários, segurança baseada em papéis (`ROLE_ALUNO` e `ROLE_INSTRUTOR`) e fluxo de autenticação.
* **`com.example.auth.matricula`**: Cadastro de matrículas, homologação de alunos e mapeamento de perfis biomecânicos (limitações articulares e estabilidade de tronco).
* **`com.example.auth.exercicio`**: Catálogo global de exercícios, exigências articulares, máquinas e sementes de dados iniciais.
* **`com.example.auth.treino`**: Gerenciamento de fichas de treino, Motor de Acessibilidade (filtragem por segurança física) e Otimizador Logístico (agrupamento por estação de trabalho).
* **`com.example.auth.clinico`**: Dossiê clínico (laudos e avaliações periódicas) e observações pedagógicas vinculadas aos treinos.

---

## 🚀 Endpoints da API

Abaixo estão os endpoints disponíveis no sistema divididos por domínio:

### 🔐 Autenticação (`/auth`)
| Método | Endpoint | Perfil Necessário | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/auth/register` | Público | Registra um novo usuário no sistema |
| **POST** | `/auth/local` | Público | Autentica um usuário e retorna o token JWT e dados do perfil |
| **POST** | `/auth/login` | Público | Autenticação padrão alternativa |
| **POST** | `/auth/refresh-token` | Público | Atualiza um token JWT expirado usando o Refresh Token |
| **GET** | `/auth/me` | Autenticado | Retorna os detalhes básicos do usuário logado |

### 📝 Matrículas & Biomecânica (`/aluno` e `/instrutor`)
| Método | Endpoint | Perfil Necessário | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/aluno/matricula` | `ROLE_ALUNO` | Aluno preenche sua própria ficha de matrícula |
| **GET** | `/aluno/matricula` | `ROLE_ALUNO` | Obtém os dados de matrícula do próprio aluno logado |
| **GET** | `/instrutor/matriculas/pendentes` | `ROLE_INSTRUTOR` | Lista alunos com matrícula aguardando homologação |
| **POST** | `/instrutor/matriculas/{alunoId}/homologar` | `ROLE_INSTRUTOR` | Homologa a matrícula do aluno selecionado |
| **POST** | `/instrutor/alunos/{alunoId}/perfil-biomecanico` | `ROLE_INSTRUTOR` | Mapeia estabilidade de tronco e restrições articulares do aluno |

### 🏋️ Catálogo de Exercícios (`/exercicios`)
| Método | Endpoint | Perfil Necessário | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/exercicios` | `ROLE_INSTRUTOR` | Cadastra um novo exercício no catálogo global |
| **GET** | `/exercicios` | Qualquer Perfil | Lista todos os exercícios cadastrados no sistema |

### 📅 Fichas de Treino (`/treinos` e `/treino-diario`)
| Método | Endpoint | Perfil Necessário | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/instrutor/alunos/{alunoId}/treinos` | `ROLE_INSTRUTOR` | Cria e vincula uma ficha de treino para o aluno |
| **GET** | `/aluno/treino-diario` | `ROLE_ALUNO` | Retorna o treino do dia adaptado e reordenado em tempo real |

### 🏥 Clínico & Observações Pedagógicas (Módulo Clínico)
| Método | Endpoint | Perfil Necessário | Descrição |
| :--- | :--- | :--- | :--- |
| **POST** | `/instrutor/alunos/{alunoId}/dossie-clinico` | `ROLE_INSTRUTOR` | Cadastra exames, laudos e reavaliações médicas do aluno |
| **POST** | `/instrutor/treinos/{treinoExercicioId}/observacoes` | `ROLE_INSTRUTOR` | Adiciona orientações pedagógicas para a execução de um exercício da ficha |

---

## 💻 Como Executar Localmente

### 1. Pré-requisitos
* **Java SDK 17** instalado e configurado no PATH
* **Maven** instalado e configurado no PATH
* **PostgreSQL** instalado e executando na porta padrão (`5432`)

### 2. Configuração do Banco de Dados
A aplicação possui um inicializador automático (`AuthServiceApplication.java`) que verifica a existência do banco de dados `omni_gym` e o cria de forma transparente caso ainda não exista no servidor local do PostgreSQL.

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
2. Executar o **`DataSeedRunner`** para preencher automaticamente as tabelas de `Articulacao` e `Acessorio` com as opções padrões exigidas pelas regras de negócio.

### 4. Executando a Suíte de Testes
Para validar a integridade dos controllers, regras de acessibilidade e segurança, rode:

```bash
mvn clean test
```
