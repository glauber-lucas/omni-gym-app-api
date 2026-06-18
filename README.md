# 🏋️‍♂️ Omni Gym — Ecossistema Digital de Academia

📌 **Omni Gym** é uma plataforma moderna e integrada de gestão de academias e otimização de treinos, focada em acessibilidade biomecânica e controle logístico. O ecossistema contém aplicações móveis (Expo), aplicações web responsivas (Vite + React) e um backend robusto (Spring Boot + PostgreSQL).

---

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![React Native](https://img.shields.io/badge/React_Native-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Expo](https://img.shields.io/badge/Expo-000020?style=for-the-badge&logo=expo&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-646CFF?style=for-the-badge&logo=vite&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)

---

## 🗂️ Estrutura do Ecossistema

O repositório está organizado nos seguintes módulos:

### 🖥️ Backends
*   **[`omni-gym-api`](file:///c:/fatec/projeto%20les/omni-gym-api)**: **Backend principal** desenvolvido em Spring Boot 3.1.6 com PostgreSQL. Inclui regras de negócio para matrículas, motor de segurança biomecânica, controle clínico (laudos/dossiês), módulo financeiro com gateway de pagamento e gerenciador de treinos.
*   **[`auth-service`](file:///c:/fatec/projeto%20les/auth-service)**: Serviço legando/secundário de autenticação baseado em Spring Boot 3.1.6 com banco H2 em memória.

### 🌐 Portais Web (Vite + React + TS + TailwindCSS)
*   **[`omni-gym-aluno`](file:///c:/fatec/projeto%20les/omni-gym-aluno)**: Portal web interativo do aluno para gerenciar dados pessoais, consultar treinos e faturas.
*   **[`omni-gym-professor`](file:///c:/fatec/projeto%20les/omni-gym-professor)**: Portal web administrativo para professores e equipe de recepção gerenciarem alunos, cadastros de exercícios, financeiro e homologações de matrículas.

### 📱 Aplicativos Mobile (Expo + React Native + TS + NativeWind)
*   **[`omni-gym-app-aluno`](file:///c:/fatec/projeto%20les/omni-gym-app-aluno)**: Aplicativo móvel para os alunos visualizarem a rotina diária de treinos, adaptada em tempo real com base no perfil biomecânico de cada um.
*   **[`omni-gym-app-professor`](file:///c:/fatec/projeto%20les/omni-gym-app-professor)**: Aplicativo móvel para professores acompanharem o progresso e fichas de treinamento dos alunos em tempo de execução.

---

## ⚙️ Requisitos do Sistema

Certifique-se de possuir instalado:
1.  **Node.js (LTS v18 ou superior)**
2.  **pnpm (v11.1.2)** (recomendado ativar via Corepack: `corepack prepare pnpm@11.1.2 --activate`)
3.  **Java JDK 17** e **Maven 3.x**
4.  **Docker & Docker Compose** (opção recomendada e mais rápida para o backend)
5.  **Expo Go** em seu smartphone para testar os apps mobile.

---

## 🚀 Como Iniciar o Projeto

### 🐳 Rodando o Backend via Docker Compose (Recomendado)

Na pasta raiz do projeto, execute o Docker Compose para subir o banco de dados PostgreSQL e a API automaticamente:

```bash
docker compose up --build
```

A API estará disponível na porta `8080`, e o banco de dados será inicializado e estruturado automaticamente.

---

### ☕ Rodando os Backends Nativamente

#### 1. Módulo Principal (`omni-gym-api`):
Acesse a pasta da API, configure suas credenciais do PostgreSQL local no arquivo `src/main/resources/application.yml` e execute:
```bash
cd omni-gym-api
mvn spring-boot:run
```

#### 2. Módulo de Autenticação Secundário (`auth-service`):
```bash
cd auth-service
mvn spring-boot:run
```
*   Utiliza H2 (banco em memória).
*   Porta padrão: `8080`.

---

### 🌐 Rodando os Portais Web (Vite + React)

Acesse a pasta de um dos portais (Aluno ou Professor):

```bash
cd omni-gym-aluno
# ou: cd omni-gym-professor
```

1.  Copie as variáveis de ambiente base:
    ```bash
    cp .env.example .env
    ```
2.  Instale as dependências:
    ```bash
    npm install
    ```
3.  Inicie em modo de desenvolvimento:
    ```bash
    npm run dev
    ```

---

### 📱 Rodando os Aplicativos Móveis (Expo)

Acesse a pasta de um dos aplicativos:

```bash
cd omni-gym-app-aluno
# ou: cd omni-gym-app-professor
```

1.  Instale as dependências usando o `pnpm`:
    ```bash
    pnpm install
    ```
2.  Inicie o servidor Expo:
    ```bash
    pnpm start
    ```
3.  Escaneie o QR Code no terminal usando o aplicativo **Expo Go** em seu celular, ou pressione `a` (Android) / `i` (iOS) para executar em emulador.

---

## 🔒 Visão Geral das APIs (Endpoints Principais)

### Autenticação (`/auth` e `/auth/local`)
*   `POST /auth/register`: Registro de novos alunos ou instrutores.
*   `POST /auth/local` ou `/auth/login`: Autentica usuários e retorna tokens JWT de acesso.
*   `POST /auth/refresh-token`: Renova o token de acesso de 1 hora usando o refresh token (válido por 7 dias).

### Biomecânica & Matrículas
*   `POST /aluno/matricula`: Aluno preenche a anamnese/matrícula inicial.
*   `POST /instrutor/matriculas/{alunoId}/homologar`: Professor homologa e ativa o aluno no sistema.
*   `POST /instrutor/alunos/{alunoId}/perfil-biomecanico`: Cadastro de restrições articulares e limites musculares do aluno.

### Exercícios & Treinos
*   `POST /exercicios`: Cadastro de exercícios (suporta imagens via multipart upload).
*   `GET /aluno/treino-diario`: Retorna o treino do dia adaptado automaticamente pelo motor biomecânico.

### Módulo Clínico & Financeiro
*   `POST /aluno/documentos-medicos/upload`: Upload seguro de laudos médicos do aluno.
*   `POST /aluno/financeiro/faturas/{faturaId}/checkout`: Geração de pagamento via gateway integrado para faturamento próprio.
