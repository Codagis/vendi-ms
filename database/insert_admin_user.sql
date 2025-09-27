-- Script para inserir usuário administrador manualmente
-- Execute este script se o DataInitializationService não funcionar

-- Inserir usuário administrador
INSERT INTO usuarios (
    id, nome, username, cracha, email, senha, ativo, root, 
    conta_bloqueada, tentativas_login_falhadas, ultimo_login,
    perfil_id, empresa_id, loja_id, created_at, updated_at, deleted
) VALUES (
    1, 'Administrador do Sistema', 'admin', 'ADM001', 'admin@vendi.com.br', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', -- admin123
    true, true, false, 0, null, 1, 1, 1, NOW(), NOW(), false
);

-- Verificar se foi inserido
SELECT * FROM usuarios WHERE username = 'admin';

