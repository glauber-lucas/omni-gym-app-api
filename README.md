# Omni Gym

Ecossistema web para gestao de academia com foco em acessibilidade biomecanica, acompanhamento de alunos, prescricao de treinos adaptados, controle financeiro e gestao de documentos medicos.

O repositorio e dividido em tres projetos principais:

| Projeto | Descricao | Porta padrao |
| --- | --- | --- |
| `omni-gym-api` | Backend Java/Spring Boot com API REST, JWT, PostgreSQL e regras de negocio | `8080` |
| `omni-gym-professor` | Frontend React/Vite para instrutores e administracao da academia | `5174` |
| `omni-gym-aluno` | Frontend React/Vite para alunos acompanharem matricula, treino, documentos e financeiro | `5173` |

## Estrutura

```text
.
├── docker-compose.yml
├── omni-gym-api/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/example/omnigym/
├── omni-gym-professor/
│   ├── package.json
│   └── src/
└── omni-gym-aluno/
    ├── package.json
    └── src/
```

## Stack

### Backend

- Java 17
- Spring Boot 3.1.6
- Spring Security com JWT e refresh token
- Spring Data JPA + Hibernate
- PostgreSQL 15
- Maven
- Containerizacao com Podman/Docker Compose

### Frontends

- React + TypeScript + Vite
- React Router
- TanStack Query
- Axios com interceptor de JWT e refresh token
- React Hook Form + Zod
- Tailwind CSS
- Lucide React
- Vitest + Testing Library

## Como Rodar Localmente

### 1. Backend com Podman

Na raiz do repositorio:

```bash
podman compose up -d --build
```

Servicos expostos:

- API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`
- Banco: `omni_gym`
- Usuario: `postgres`
- Senha: `123`

Para acompanhar logs:

```bash
podman compose logs -f api
```

Para recriar apenas a API depois de alterar o backend:

```bash
podman compose up -d --force-recreate --build api
```

Para parar os containers:

```bash
podman compose down
```

Para parar e apagar volumes de banco/uploads:

```bash
podman compose down -v
```

### 2. Frontend do Aluno

```bash
cd omni-gym-aluno
npm install
npm run dev
```

URL padrao:

```text
http://localhost:5173
```

Arquivo de ambiente:

```bash
cp .env.example .env
```

Conteudo esperado:

```env
VITE_API_BASE_URL=http://localhost:8080
```

### 3. Frontend do Professor

```bash
cd omni-gym-professor
npm install
npm run dev
```

URL padrao:

```text
http://localhost:5174
```

Arquivo de ambiente:

```bash
cp .env.example .env
```

Conteudo esperado:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_INSTRUCTOR_SECRET=secret-instructor-key
```

### 4. Usando Distrobox

Se o ambiente de desenvolvimento com Node/Java estiver no distrobox `dev-mobile`, execute os frontends assim:

```bash
distrobox enter dev-mobile -- bash -lc 'cd /var/home/pfranco/Documentos/Projetos/Fatec/omni-gym-app-api/omni-gym-aluno && npm run dev'
```

```bash
distrobox enter dev-mobile -- bash -lc 'cd /var/home/pfranco/Documentos/Projetos/Fatec/omni-gym-app-api/omni-gym-professor && npm run dev'
```

## Backend

O backend centraliza autenticacao, seguranca, cadastro de alunos, catalogo de exercicios, treinos, documentos medicos e financeiro.

### Principais modulos

| Pacote | Responsabilidade |
| --- | --- |
| `core` | Configuracoes transversais, seguranca JWT, CORS e tratamento de excecoes |
| `user` | Registro, login, refresh token, usuarios e papeis (`ROLE_ALUNO`, `ROLE_INSTRUTOR`) |
| `matricula` | Cadastro do aluno, homologacao e perfil biomecanico |
| `exercicio` | Exercicios, articulacoes, acessorios, adaptacoes e imagens dos exercicios |
| `treino` | Fichas de treino, treino diario, exercicios disponiveis e motor de acessibilidade |
| `clinico` | Documentos medicos, auditoria de acesso e observacoes pedagogicas |
| `financeiro` | Planos, assinaturas, faturas, pagamentos e relatorios |

### Autenticacao

O fluxo usa JWT:

- `POST /auth/register`: cria usuario aluno ou instrutor.
- `POST /auth/local`: autentica por usuario/senha.
- `POST /auth/refresh-token`: renova token de acesso.
- `GET /auth/me`: retorna usuario autenticado.

Instrutores precisam informar a chave:

```text
secret-instructor-key
```

### Exercicios e imagens

Instrutores cadastram exercicios no catalogo global. Cada exercicio pode ter:

- nome;
- grupo muscular;
- estacao de trabalho;
- estabilidade minima de tronco;
- exigencias articulares;
- adaptacao opcional;
- imagem representativa.

Fluxo usado pelo frontend do professor:

1. `POST /exercicios` com JSON para criar o exercicio.
2. `POST /exercicios/{id}/imagem` com `multipart/form-data` para enviar a imagem, quando houver.

Visualizacao:

- `GET /exercicios/{id}/imagem` retorna a imagem protegida por JWT.
- O professor ve a imagem nos cards do catalogo pelo botao de olho.
- O aluno ve a imagem nos exercicios disponiveis pelo botao de olho.

### Treinos e acessibilidade

O instrutor cria fichas para alunos homologados. O backend classifica exercicios conforme:

- estabilidade de tronco do aluno;
- restricoes articulares;
- bloqueio medico;
- adaptacoes e acessorios cadastrados.

O aluno consegue consultar o treino diario e editar a ficha ativa apenas com exercicios permitidos pelo motor de acessibilidade.

### Documentos medicos

O modulo clinico permite upload, download seguro e auditoria de documentos:

- upload via `multipart/form-data`;
- validacao de propriedade do aluno;
- historico de acesso;
- soft delete.

### Financeiro

O modulo financeiro contempla:

- planos;
- assinaturas;
- faturas;
- descontos;
- pagamento manual pelo instrutor;
- consulta financeira pelo aluno;
- relatorio de faturamento.

## Frontend do Professor

Local: `omni-gym-professor`

Portal usado por instrutores para administrar alunos, catalogo, treinos, documentos e financeiro.

### Funcionalidades

- Login e cadastro de instrutor com chave local.
- Dashboard administrativo.
- Listagem de matriculas e alunos pendentes.
- Homologacao de matricula.
- Mapeamento de perfil biomecanico.
- Cadastro de articulacoes e acessorios.
- Cadastro de exercicios com upload de imagem.
- Visualizacao da imagem do exercicio em modal.
- Criacao de fichas de treino para alunos.
- Observacoes pedagogicas.
- Gestao financeira com planos, faturas e assinaturas.
- Consulta de documentos medicos e auditoria.

### Scripts

```bash
npm run dev
npm run typecheck
npm run lint
npm run test
npm run build
npm run preview
```

### Rotas principais

As rotas sao protegidas por autenticacao. O token e salvo no `localStorage` e anexado automaticamente pelo Axios.

## Frontend do Aluno

Local: `omni-gym-aluno`

Portal usado pelo aluno para acompanhar cadastro, treino, documentos e financeiro.

### Funcionalidades

- Login e cadastro de aluno.
- Dashboard com status da matricula, estabilidade, exercicios do dia e documentos.
- Formulario de matricula.
- Consulta do treino diario.
- Edicao da ficha ativa respeitando exercicios permitidos.
- Listagem de exercicios disponiveis.
- Visualizacao da imagem do exercicio em modal.
- Upload de documentos medicos.
- Consulta de faturas.
- Simulacao/confirmacao de pagamento conforme endpoints disponiveis.

### Scripts

```bash
npm run dev
npm run typecheck
npm run lint
npm run test
npm run build
npm run preview
```

## Qualidade e Verificacao

### Backend

Dentro de `omni-gym-api`:

```bash
mvn clean test
```

Ou validar via build do container:

```bash
podman compose up -d --force-recreate --build api
```

### Frontend Professor

```bash
cd omni-gym-professor
npm run typecheck
npm run test
npm run build
```

### Frontend Aluno

```bash
cd omni-gym-aluno
npm run typecheck
npm run test
npm run build
```

## Variaveis e Configuracoes

### Backend

As configuracoes principais ficam em `omni-gym-api/src/main/resources/application.yml` e tambem podem ser sobrescritas pelo `docker-compose.yml`.

Principais variaveis usadas no compose:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/omni_gym
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=123
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

Uploads sao salvos no volume `api-uploads`, montado em:

```text
/app/uploads
```

### Frontends

Aluno:

```env
VITE_API_BASE_URL=http://localhost:8080
```

Professor:

```env
VITE_API_BASE_URL=http://localhost:8080
VITE_INSTRUCTOR_SECRET=secret-instructor-key
```

## Fluxo de Uso Sugerido

1. Subir backend e banco com Podman.
2. Rodar o frontend do professor em `5174`.
3. Criar um instrutor usando `VITE_INSTRUCTOR_SECRET`.
4. Rodar o frontend do aluno em `5173`.
5. Criar um aluno e preencher matricula.
6. No professor, homologar a matricula e configurar perfil biomecanico.
7. Cadastrar articulacoes, acessorios e exercicios.
8. Opcionalmente enviar imagem para os exercicios.
9. Criar ficha de treino para o aluno.
10. No aluno, consultar treino e exercicios disponiveis.

## Troubleshooting

### Frontend nao conecta na API

Verifique:

- se a API esta em `http://localhost:8080`;
- se `.env` contem `VITE_API_BASE_URL=http://localhost:8080`;
- se o backend foi rebuildado depois de alteracoes Java;
- se o token JWT nao expirou.

### Upload de imagem nao aparece

Verifique:

- se o exercicio foi cadastrado antes do upload;
- se a requisicao `POST /exercicios/{id}/imagem` retornou sucesso;
- se a API foi recriada com `podman compose up -d --force-recreate --build api`;
- se o usuario esta autenticado, pois `GET /exercicios/{id}/imagem` exige JWT.

### Banco com dados antigos

Para limpar banco e uploads:

```bash
podman compose down -v
podman compose up -d --build
```

Esse comando apaga volumes locais, incluindo dados do PostgreSQL e arquivos enviados.
