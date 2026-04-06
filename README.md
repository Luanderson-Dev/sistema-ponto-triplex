# Ponto da Triplex

Sistema de ponto eletrônico web para gerenciamento de jornada de trabalho. Permite que funcionários registrem entrada e saída, enquanto administradores acompanham os registros de toda a equipe.

## Stack

| Camada     | Tecnologia                                      |
|------------|--------------------------------------------------|
| Backend    | Java 25, Spring Boot 4, Spring Security, JWT     |
| Frontend   | Angular 21, TailwindCSS 4, SSR com Express       |
| Banco      | PostgreSQL 16, Flyway (migrations)                |
| Infra      | Docker, Nginx (reverse proxy)                     |

## Funcionalidades

- **Registro de ponto** — entrada e saída com relógio em tempo real e cálculo automático de horas trabalhadas
- **Painel administrativo** — consulta de registros por funcionário e período, com totalização de horas
- **Gestão de usuários** — cadastro com papéis (ADMIN / USER) e controle de acesso
- **Autenticação segura** — JWT (access token 15min) + refresh token em cookie HttpOnly com rotação automática
- **Troca de senha** — usuários autenticados podem alterar a própria senha
- **Admin seed** — criação automática do primeiro administrador via variáveis de ambiente

## Arquitetura

```
ponto-da-triplex/
├── api/            # Spring Boot — Arquitetura Hexagonal (Ports & Adapters)
├── frontend/       # Angular 21 — Standalone Components, Signals
├── nginx/          # Reverse proxy
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
features/        → Componentes: login, dashboard, ponto, usuarios, admin-pontos, perfil
```

## Pré-requisitos

- [Docker](https://docs.docker.com/get-docker/) e Docker Compose
- (Opcional para dev) Java 25, Node 22, Maven

## Instalação

### 1. Clone o repositório

```bash
git clone https://github.com/Luanderson-Dev/ponto-da-triplex.git
cd ponto-da-triplex
```

### 2. Configure as variáveis de ambiente

```bash
cp .env.example .env
```

Edite o `.env` com seus valores:

```env
# PostgreSQL
POSTGRES_USER=postgres
POSTGRES_PASSWORD=sua-senha-segura
POSTGRES_DB=ponto_db

# Segurança
SECURITY_PEPPER=sua-pepper-secreta-aqui
JWT_SEGREDO=sua-chave-jwt-com-pelo-menos-256-bits-aqui

# Admin inicial
ADMIN_NOME=Administrador
ADMIN_EMAIL=admin@example.com
ADMIN_SENHA=senha-do-admin

# Porta da aplicação
APP_PORT=80
```

### 3. Suba os containers

```bash
docker compose up -d --build
```

A aplicação estará disponível em `http://localhost` (ou na porta configurada em `APP_PORT`).

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

O dev server roda em `http://localhost:4200` com proxy automático para a API (`proxy.conf.json`).

## Endpoints da API

| Método | Rota                  | Acesso        | Descrição                     |
|--------|-----------------------|---------------|-------------------------------|
| POST   | `/auth/login`         | Público       | Autenticação                  |
| POST   | `/auth/refresh`       | Cookie        | Renovar access token          |
| POST   | `/auth/logout`        | Cookie        | Encerrar sessão               |
| POST   | `/api/pontos/entrada` | Autenticado   | Registrar entrada             |
| POST   | `/api/pontos/saida`   | Autenticado   | Registrar saída               |
| GET    | `/api/pontos/aberto`  | Autenticado   | Verificar ponto aberto        |
| GET    | `/api/pontos/meus`    | Autenticado   | Listar meus registros         |
| GET    | `/api/pontos/admin`   | Admin         | Listar registros (filtros)    |
| POST   | `/api/usuarios`       | Admin         | Criar usuário                 |
| GET    | `/api/usuarios`       | Admin         | Listar usuários               |

## Banco de dados

O schema é gerenciado pelo Flyway. As migrations estão em `api/src/main/resources/db/migration/`.

**Tabelas:**

- `usuarios` — id, nome, email (unique), senha_hash, role
- `registros_ponto` — id, usuario_id (FK), hora_entrada, hora_saida
- `refresh_tokens` — id, token (unique), usuario_id (FK), criado_em, expira_em, revogado

## Segurança

- Senhas hasheadas com **Argon2** + pepper
- Access tokens JWT com expiração de 15 minutos
- Refresh tokens em cookies **HttpOnly, Secure, SameSite=Strict** com validade de 7 dias
- Rotação automática de refresh tokens
- Proteção contra CSRF via cookies SameSite
- Sessões stateless (sem estado no servidor)

## Licença

Este projeto está licenciado sob a [MIT License](LICENSE).
