-- Script para verificar e criar usuário admin
-- Execute após o DataInitializationService rodar

-- Conectar ao banco vendi-dev
\c vendi-dev;

-- Verificar se o usuário admin existe
SELECT 
    id, nome, username, email, ativo, root, 
    conta_bloqueada, tentativas_login_falhadas
FROM usuarios 
WHERE username = 'admin';

-- Se não existir, criar manualmente
-- (Execute apenas se o usuário não existir)

-- Primeiro, verificar se as tabelas existem
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name IN ('usuarios', 'perfis', 'permissoes', 'empresas', 'lojas');

-- Verificar se há dados nas tabelas
SELECT 'usuarios' as tabela, COUNT(*) as total FROM usuarios
UNION ALL
SELECT 'perfis' as tabela, COUNT(*) as total FROM perfis
UNION ALL
SELECT 'permissoes' as tabela, COUNT(*) as total FROM permissoes
UNION ALL
SELECT 'empresas' as tabela, COUNT(*) as total FROM empresas
UNION ALL
SELECT 'lojas' as tabela, COUNT(*) as total FROM lojas;
