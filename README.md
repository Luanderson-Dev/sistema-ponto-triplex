# Ponto da Triplex

Sistema de ponto eletrônico web para gerenciamento de jornada de trabalho. Funcionários registram entrada e saída via interface web ou automaticamente pelo bot do Discord ao entrar em canais de voz. Administradores (RH) acompanham os registros de toda a equipe.

## Stack

| Camada         | Tecnologia                                          |
|----------------|-----------------------------------------------------|
| Backend        | Java 25, Spring Boot 4, Spring Security, JWT        |
| Frontend       | Angular 21, TailwindCSS 4, SSR com Express          |
| Banco de Dados | PostgreSQL 16, Flyway (migrations)                  |
| Infra          | Docker, Docker Compose, Nginx (reverse proxy)       |
| Autenticação   | Discord OAuth2, JWT (access + refresh token)        |
| CI/CD          | GitHub Actions (testes), Jenkins (deploy)            |

## Funcionalidades

- **Registro de ponto** — entrada e saída com relógio em tempo real e cálculo automático de horas trabalhadas
- **Registro via Discord** — ponto automático ao entrar/sair de canais de voz (integração com [Triplex Bot](https://github.com/Luanderson-Dev/triplex-bot))
- **Login com Discord** — autenticação OAuth2 com verificação de membro do servidor
- **Painel do funcionário** — visualização dos próprios registros, turno aberto e horas acumuladas
- **Painel administrativo (RH)** — consulta de registros por funcionário e período com totalização de horas
- **Gestão de usuários** — cadastro automático via Discord com papéis (RH / USER)
- **Autenticação segura** — JWT (access token 15min) + refresh token em cookie HttpOnly com rotação automática

## Arquitetura

```
ponto-da-triplex/
├── api/               # Spring Boot — Arquitetura Hexagonal (Ports & Adapters)
├── frontend/          # Angular 21 — Standalone Components, Signals
├── nginx/             # Reverse proxy
├── Jenkinsfile        # Pipeline de deploy (Jenkins)
└── docker-compose.yml
```

### Backend (Hexagonal)

```
domain/          → Entidades puras (Usuario, RegistroPonto, RefreshToken)
application/     → Use cases e ports (interfaces)
infrastructure/  → Controllers, JPA adapters, segurança, config
```

### Frontend

```
core/auth/       → AuthService (signals), interceptor JWT, guards
core/services/   → PontoService, UsuarioService
core/models/     → Interfaces TypeScript
features/        → login, dashboard, ponto, usuarios, admin-pontos
```

## Pré-requisitos

- [Docker](https://docs.docker.com/get-docker/) e Docker Compose
- (Opcional para dev) Java 25, Node 22, Maven

## Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/Luanderson-Dev/sistema-ponto-triplex.git
cd sistema-ponto-triplex
```

### 2. Configure as variáveis de ambiente

```bash
cp .env.example .env
```

Edite o `.env`:

```env
# PostgreSQL
POSTGRES_USER=postgres
POSTGRES_PASSWORD=sua-senha-segura
POSTGRES_DB=ponto_db

# Segurança
JWT_SEGREDO=sua-chave-jwt-com-pelo-menos-256-bits

# Discord OAuth
DISCORD_CLIENT_ID=seu-client-id
DISCORD_CLIENT_SECRET=seu-client-secret
DISCORD_REDIRECT_URI=https://seu-dominio/auth/discord/callback
DISCORD_GUILD_ID=id-do-servidor-discord

# Integração com bot
BOT_API_KEY=chave-compartilhada-com-o-bot
```

### 3. Suba os containers

```bash
docker compose up -d --build
```

A aplicação estará disponível em `http://localhost` (ou na porta configurada).

## Desenvolvimento local

### Backend

```bash
cd api
docker compose up -d   # sobe apenas o PostgreSQL
./mvnw spring-boot:run
```

A API roda em `http://localhost:8080`. Documentação Swagger disponível em `/swagger-ui.html`.

### Frontend

```bash
cd frontend
npm install
npm start
```

O dev server roda em `http://localhost:4200` com proxy automático para a API.

## Endpoints da API

### Autenticação

| Método | Rota                      | Acesso  | Descrição                    |
|--------|---------------------------|---------|------------------------------|
| GET    | `/auth/discord/url`       | Público | Obter URL de login Discord   |
| POST   | `/auth/discord/token`     | Público | Trocar code por tokens       |
| POST   | `/auth/refresh`           | Cookie  | Renovar access token         |
| POST   | `/auth/logout`            | Cookie  | Encerrar sessão              |

### Ponto

| Método | Rota                      | Acesso      | Descrição                  |
|--------|---------------------------|-------------|----------------------------|
| POST   | `/api/pontos/entrada`     | Autenticado | Registrar entrada          |
| POST   | `/api/pontos/saida`       | Autenticado | Registrar saída            |
| GET    | `/api/pontos/aberto`      | Autenticado | Verificar ponto aberto     |
| GET    | `/api/pontos/meus`        | Autenticado | Listar meus registros      |
| GET    | `/api/pontos/admin`       | RH          | Listar registros (filtros) |

### Usuários

| Método | Rota                      | Acesso | Descrição        |
|--------|---------------------------|--------|------------------|
| GET    | `/api/usuarios`           | RH     | Listar usuários  |

### API interna (Bot)

| Método | Rota                           | Acesso  | Descrição                   |
|--------|--------------------------------|---------|-----------------------------|
| POST   | `/api/internal/ponto/entrada`  | API Key | Registrar entrada via bot   |
| POST   | `/api/internal/ponto/saida`    | API Key | Registrar saída via bot     |

## Banco de dados

O schema é gerenciado pelo Flyway. As migrations estão em `api/src/main/resources/db/migration/`.

**Tabelas:**

- `usuarios` — id, nome, email (unique), senha_hash, role, discord_id (unique)
- `registros_ponto` — id, usuario_id (FK), hora_entrada, hora_saida
- `refresh_tokens` — id, token (unique), usuario_id (FK), criado_em, expira_em, revogado

## Segurança

- Access tokens JWT com expiração de 15 minutos
- Refresh tokens em cookies **HttpOnly, Secure, SameSite=Strict** com validade de 7 dias
- Rotação automática de refresh tokens
- Autenticação Discord OAuth2 com verificação de guild
- API interna protegida por API Key (header `X-Bot-Api-Key`)
- Sessões stateless (sem estado no servidor)

## CI/CD

- **GitHub Actions** — testes automáticos em push/PR na `main` (Maven verify + build do frontend)
- **Jenkins** — deploy automático em produção via webhook ao detectar push na `main`

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).
