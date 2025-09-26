# Configuração de Ambientes - Vendi MS

Este projeto utiliza profiles do Spring Boot para gerenciar diferentes configurações de ambiente.

## Profiles Disponíveis

### 1. **dev** (Desenvolvimento) - Padrão
- **Arquivo**: `application-dev.yml`
- **Banco**: `vendi-dev`
- **Porta**: 8080
- **DDL**: `update` (atualiza schema)
- **Logs**: DEBUG habilitado
- **Usuário**: admin/admin123

### 2. **prod** (Produção)
- **Arquivo**: `application-prod.yml`
- **Banco**: Configurável via variáveis de ambiente
- **Porta**: Configurável via `SERVER_PORT`
- **DDL**: `validate` (apenas valida schema)
- **Logs**: WARN/INFO apenas
- **Usuário**: Configurável via variáveis de ambiente

### 3. **test** (Teste)
- **Arquivo**: `application-test.yml`
- **Banco**: `vendi-test`
- **Porta**: 8081
- **DDL**: `create-drop` (cria e remove schema)
- **Logs**: DEBUG habilitado
- **Usuário**: test/test123

### 4. **local** (Local)
- **Arquivo**: `application-local.yml`
- **Banco**: `vendi-local`
- **Porta**: 8080
- **DDL**: `create-drop` (cria e remove schema)
- **Logs**: DEBUG habilitado
- **Usuário**: local/local123

### 5. **docker** (Docker)
- **Arquivo**: `application-docker.yml`
- **Banco**: Configurável via variáveis de ambiente
- **Porta**: Configurável via `SERVER_PORT`
- **DDL**: `update`
- **Logs**: INFO
- **Usuário**: Configurável via variáveis de ambiente

## Como Usar

### 1. Via Linha de Comando
```bash
# Desenvolvimento (padrão)
mvn spring-boot:run

# Produção
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Teste
mvn spring-boot:run -Dspring-boot.run.profiles=test

# Local
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Docker
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

### 2. Via Variável de Ambiente
```bash
# Windows
set SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run

# Linux/Mac
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

### 3. Via application.properties (temporário)
```properties
spring.profiles.active=prod
```

### 4. Via IDE
- **IntelliJ IDEA**: Run Configuration → Environment Variables → `SPRING_PROFILES_ACTIVE=prod`
- **Eclipse**: Run Configuration → Environment → `SPRING_PROFILES_ACTIVE=prod`
- **VS Code**: launch.json → env → `SPRING_PROFILES_ACTIVE=prod`

## Variáveis de Ambiente para Produção

```bash
# Banco de Dados
DB_HOST=localhost
DB_PORT=5432
DB_NAME=vendi-prod
DB_USERNAME=postgres
DB_PASSWORD=senha_segura

# Aplicação
SERVER_PORT=8080
ADMIN_USERNAME=admin
ADMIN_PASSWORD=senha_admin_segura
```

## Estrutura de Arquivos

```
src/main/resources/
├── application.yml          # Configurações comuns e profile padrão
├── application-dev.yml      # Desenvolvimento
├── application-prod.yml     # Produção
├── application-test.yml     # Teste
├── application-local.yml    # Local
├── application-docker.yml   # Docker
└── README-PROFILES.md       # Esta documentação
```

## Configurações por Ambiente

### Desenvolvimento (dev)
- Logs detalhados
- DDL automático
- Endpoints de management expostos
- Configurações de debug habilitadas

### Produção (prod)
- Logs mínimos
- DDL apenas validação
- Configurações de performance
- Variáveis de ambiente obrigatórias
- Endpoints de management restritos

### Teste (test)
- Schema recriado a cada execução
- Logs detalhados
- Configurações isoladas
- Porta diferente (8081)

### Local (local)
- Schema recriado a cada execução
- Logs muito detalhados
- Configurações de desenvolvimento local

### Docker (docker)
- Configurações para containers
- Variáveis de ambiente para orquestração
- Logs balanceados
