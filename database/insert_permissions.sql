-- Script para inserir permissões do sistema
-- Execute este script após criar as tabelas

-- Inserir permissões básicas do sistema
INSERT INTO permissoes (chave, nome, descricao, categoria, ativo, created_at, updated_at, deleted) VALUES
-- Permissões de Sistema
('dashboard', 'Dashboard', 'Acesso ao painel principal do sistema', 'Sistema', true, NOW(), NOW(), false),
('settings', 'Configurações', 'Acesso às configurações do sistema', 'Sistema', true, NOW(), NOW(), false),
('users', 'Usuários', 'Gerenciamento de usuários do sistema', 'Sistema', true, NOW(), NOW(), false),
('perfis', 'Perfis', 'Gerenciamento de perfis de usuário', 'Sistema', true, NOW(), NOW(), false),
('permissoes', 'Permissões', 'Gerenciamento de permissões do sistema', 'Sistema', true, NOW(), NOW(), false),

-- Permissões de Vendas
('pos', 'PDV - Vendas', 'Acesso ao sistema de vendas (PDV)', 'Vendas', true, NOW(), NOW(), false),

-- Permissões de Cadastros
('products', 'Produtos', 'Gerenciamento de produtos', 'Cadastros', true, NOW(), NOW(), false),
('customers', 'Clientes', 'Gerenciamento de clientes', 'Cadastros', true, NOW(), NOW(), false),
('customers_view', 'Clientes (Apenas Visualizar)', 'Apenas visualização de clientes', 'Cadastros', true, NOW(), NOW(), false),

-- Permissões de Operações
('inventory', 'Estoque', 'Gerenciamento de estoque', 'Operações', true, NOW(), NOW(), false),

-- Permissões Financeiras
('financial', 'Financeiro', 'Acesso ao módulo financeiro', 'Financeiro', true, NOW(), NOW(), false),

-- Permissões de Relatórios
('reports', 'Relatórios', 'Acesso aos relatórios do sistema', 'Relatórios', true, NOW(), NOW(), false),
('reports_view', 'Relatórios (Apenas Visualizar)', 'Apenas visualização de relatórios', 'Relatórios', true, NOW(), NOW(), false),

-- Permissão especial para acesso total
('all', 'Acesso Total', 'Acesso a todas as funcionalidades do sistema', 'Sistema', true, NOW(), NOW(), false);

-- Atualizar perfis existentes para incluir as novas permissões
-- Perfil Administrador - acesso total
UPDATE perfis SET permissoes = (
    SELECT ARRAY_AGG(p.id) FROM permissoes p 
    WHERE p.ativo = true AND p.deleted = false
) WHERE codigo = 'admin';

-- Perfil Vendedor - permissões básicas de vendas
UPDATE perfis SET permissoes = (
    SELECT ARRAY_AGG(p.id) FROM permissoes p 
    WHERE p.chave IN ('dashboard', 'pos', 'customers', 'customers_view', 'reports_view')
    AND p.ativo = true AND p.deleted = false
) WHERE codigo = 'seller';

-- Perfil Estoquista - permissões de estoque e produtos
UPDATE perfis SET permissoes = (
    SELECT ARRAY_AGG(p.id) FROM permissoes p 
    WHERE p.chave IN ('dashboard', 'inventory', 'products', 'reports_view')
    AND p.ativo = true AND p.deleted = false
) WHERE codigo = 'stock';

-- Perfil Financeiro - permissões financeiras
UPDATE perfis SET permissoes = (
    SELECT ARRAY_AGG(p.id) FROM permissoes p 
    WHERE p.chave IN ('dashboard', 'financial', 'reports', 'customers_view')
    AND p.ativo = true AND p.deleted = false
) WHERE codigo = 'financial';

-- Verificar se as permissões foram inseridas corretamente
SELECT 'Permissões inseridas:' as status, COUNT(*) as total FROM permissoes WHERE deleted = false;
SELECT 'Perfis atualizados:' as status, COUNT(*) as total FROM perfis WHERE deleted = false;

