-- Script para configurar o banco de dados Vendi
-- Execute como superuser do PostgreSQL

-- Criar banco de dados se não existir
CREATE DATABASE "vendi-dev" 
WITH 
    ENCODING = 'UTF8'
    LC_COLLATE = 'pt_BR.UTF-8'
    LC_CTYPE = 'pt_BR.UTF-8'
    TEMPLATE = template0;

-- Conectar ao banco vendi-dev
\c vendi-dev;

-- Criar usuário se não existir
DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'postgres') THEN
        CREATE ROLE postgres LOGIN PASSWORD 'postgres' SUPERUSER CREATEDB CREATEROLE;
    END IF;
END
$$;

-- Dar permissões ao usuário
GRANT ALL PRIVILEGES ON DATABASE "vendi-dev" TO postgres;
GRANT ALL PRIVILEGES ON SCHEMA public TO postgres;

-- Verificar se o banco foi criado
SELECT datname FROM pg_database WHERE datname = 'vendi-dev';
