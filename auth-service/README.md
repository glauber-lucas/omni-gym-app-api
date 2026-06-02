# Auth Service (Spring Boot JWT)

Projeto de exemplo com Spring Boot 3 + Spring Security + JWT (java-jwt) usando H2 para desenvolvimento.

Como executar:

1. Build & run com Maven:

```powershell
mvn clean package
mvn spring-boot:run
```

2. Endpoints:
- POST /auth/register -> registrar (body: RegisterDTO { username (email), password })
- POST /auth/login -> autenticar (body: AuthenticationDTO { username, password })

Configuração de JWT em `src/main/resources/application.yml` (troque o segredo em produção).

