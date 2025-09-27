-- Adicionar coluna url_logo na tabela empresas
ALTER TABLE empresas ADD COLUMN url_logo VARCHAR(1000);

-- Comentário da coluna
COMMENT ON COLUMN empresas.url_logo IS 'URL da logo da empresa no Google Cloud Storage';
