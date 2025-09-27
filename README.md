# Vendi Backend - Sistema de PDV

Sistema backend para o Vendi, um sistema de ponto de venda (PDV) com gestão de estoque e financeiro.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.5.6
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JSON Web Tokens)
- Lombok
- Maven

## Configuração do Banco de Dados

### 1. Instalar PostgreSQL

Instale o PostgreSQL em sua máquina e crie um banco de dados:

```sql
CREATE DATABASE vendi_db;
CREATE USER vendi_user WITH PASSWORD 'vendi_pass';
GRANT ALL PRIVILEGES ON DATABASE vendi_db TO vendi_user;
```

### 2. Configuração da Aplicação

As configurações estão no arquivo `application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vendi_db
    username: vendi_user
    password: vendi_pass
```

## Como Executar

### 1. Pré-requisitos

- Java 17 ou superior
- Maven 3.6 ou superior
- PostgreSQL 12 ou superior

### 2. Executar a Aplicação

```bash
# Navegar para o diretório do backend
cd vendi-ms

# Executar com perfil local
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Ou compilar e executar
mvn clean package
java -jar target/vendi-ms-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

### 3. Acessar a Aplicação

- **URL Base**: http://localhost:8080
- **API**: http://localhost:8080/api

## Endpoints da API

### Autenticação

- `POST /api/auth/login` - Login de usuário
- `GET /api/auth/validate` - Validar token

### Usuários

- `GET /api/usuarios` - Listar usuários
- `GET /api/usuarios/{id}` - Buscar usuário por ID
- `POST /api/usuarios` - Criar usuário
- `PUT /api/usuarios/{id}` - Atualizar usuário
- `DELETE /api/usuarios/{id}` - Deletar usuário
- `PATCH /api/usuarios/{id}/status` - Alterar status do usuário

## Dados Iniciais

A aplicação cria automaticamente:

1. **Permissões do sistema** (dashboard, pos, customers, etc.)
2. **Perfis de usuário** (admin, seller, stock, financial)
3. **Empresa padrão** (Vendi Sistemas LTDA)
4. **Loja padrão** (Loja Matriz)
5. **Usuário administrador**:
   - Username: `admin`
   - Senha: `admin123`

## Estrutura do Projeto

```
src/main/java/com/vendi/vendi_ms/
├── config/          # Configurações (Security, JWT)
├── controller/      # Controllers REST
├── dto/            # Data Transfer Objects
├── model/          # Entidades JPA
├── repository/     # Repositories JPA
└── service/        # Services de negócio
```

## Segurança

- Autenticação via JWT
- Senhas criptografadas com BCrypt
- Controle de acesso baseado em perfis e permissões
- CORS configurado para frontend

## Desenvolvimento

### Perfis de Execução

- `local` - Desenvolvimento local com PostgreSQL
- `dev` - Ambiente de desenvolvimento
- `prod` - Ambiente de produção

### Logs

Os logs estão configurados para mostrar:
- Queries SQL (DEBUG)
- Operações de segurança (DEBUG)
- Logs da aplicação (DEBUG)

## Frontend

O frontend está localizado em `../vendi-fe` e deve ser executado separadamente:

```bash
cd ../vendi-fe
npm install
npm run dev
```

## Troubleshooting

### Erro de Conexão com Banco

1. Verifique se o PostgreSQL está rodando
2. Confirme as credenciais no `application-local.yml`
3. Verifique se o banco `vendi_db` existe

### Erro de Porta

Se a porta 8080 estiver ocupada, altere no `application-local.yml`:

```yaml
server:
  port: 8081
```

### Token JWT Inválido

1. Verifique se o token está sendo enviado no header `Authorization: Bearer <token>`
2. Confirme se o token não expirou (válido por 8 horas)
3. Verifique a configuração do JWT no `application-local.yml`

