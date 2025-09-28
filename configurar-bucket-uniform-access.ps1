# Script para configurar bucket com Uniform Bucket-Level Access
# Execute no Google Cloud Shell

Write-Host "=== Configuração Uniform Bucket-Level Access - Vendi PDV ===" -ForegroundColor Green

Write-Host "`n=== PROBLEMA IDENTIFICADO ===" -ForegroundColor Red
Write-Host "O bucket tem 'Uniform Bucket-Level Access' habilitado" -ForegroundColor Yellow
Write-Host "Isso impede configuração de ACLs individuais nos objetos" -ForegroundColor Yellow
Write-Host "As permissões devem ser configuradas via IAM no nível do bucket" -ForegroundColor Yellow

Write-Host "`n=== SOLUÇÃO ===" -ForegroundColor Green

Write-Host "`n1. Acesse o Google Cloud Shell:" -ForegroundColor Cyan
Write-Host "   https://shell.cloud.google.com" -ForegroundColor White

Write-Host "`n2. Execute os comandos:" -ForegroundColor Cyan

Write-Host "`n# Configurar projeto" -ForegroundColor Yellow
Write-Host "gcloud config set project ontimeserv" -ForegroundColor Green

Write-Host "`n# Verificar se bucket tem Uniform Bucket-Level Access" -ForegroundColor Yellow
Write-Host "gsutil uniformbucketlevelaccess get gs://vendi-pdv" -ForegroundColor Green

Write-Host "`n# Configurar acesso público via IAM (SOLUÇÃO PRINCIPAL)" -ForegroundColor Yellow
Write-Host "gsutil iam ch allUsers:objectViewer gs://vendi-pdv" -ForegroundColor Green

Write-Host "`n# Verificar permissões configuradas" -ForegroundColor Yellow
Write-Host "gsutil iam get gs://vendi-pdv" -ForegroundColor Green

Write-Host "`n=== ALTERNATIVA VIA CONSOLE WEB ===" -ForegroundColor Cyan

Write-Host "`n1. Acesse: https://console.cloud.google.com/storage" -ForegroundColor White
Write-Host "2. Clique no bucket: vendi-pdv" -ForegroundColor White
Write-Host "3. Vá para: Permissions" -ForegroundColor White
Write-Host "4. Clique em: Grant Access" -ForegroundColor White
Write-Host "5. Adicione:" -ForegroundColor White
Write-Host "   - New principals: allUsers" -ForegroundColor White
Write-Host "   - Role: Storage Object Viewer" -ForegroundColor White
Write-Host "6. Clique em: Save" -ForegroundColor White

Write-Host "`n=== VERIFICAR SE FUNCIONOU ===" -ForegroundColor Cyan

Write-Host "`n1. Faça upload de uma logo na aplicação" -ForegroundColor White
Write-Host "2. Copie a URL gerada" -ForegroundColor White
Write-Host "3. Abra no navegador" -ForegroundColor White
Write-Host "4. A imagem deve carregar normalmente" -ForegroundColor White

Write-Host "`n=== COMANDOS DE TESTE ===" -ForegroundColor Cyan

Write-Host "`n# Listar arquivos no bucket" -ForegroundColor Yellow
Write-Host "gsutil ls -r gs://vendi-pdv/logos/" -ForegroundColor Green

Write-Host "`n# Testar acesso público a um arquivo" -ForegroundColor Yellow
Write-Host "curl -I https://storage.googleapis.com/vendi-pdv/logos/[nome-empresa]/[arquivo]" -ForegroundColor Green

Write-Host "`n# Verificar permissões do bucket" -ForegroundColor Yellow
Write-Host "gsutil iam get gs://vendi-pdv" -ForegroundColor Green

Write-Host "`n=== EXPLICAÇÃO TÉCNICA ===" -ForegroundColor Cyan

Write-Host "`nUniform Bucket-Level Access:" -ForegroundColor White
Write-Host "- Gerencia permissões no nível do bucket" -ForegroundColor White
Write-Host "- Não permite ACLs individuais nos objetos" -ForegroundColor White
Write-Host "- Mais seguro e moderno" -ForegroundColor White
Write-Host "- Permissões via IAM (Identity and Access Management)" -ForegroundColor White

Write-Host "`nConfiguração IAM:" -ForegroundColor White
Write-Host "- allUsers:objectViewer = Acesso público de leitura" -ForegroundColor White
Write-Host "- Aplica a todos os objetos do bucket" -ForegroundColor White
Write-Host "- Não precisa configurar por arquivo" -ForegroundColor White

Write-Host "`n=== STATUS ATUAL ===" -ForegroundColor Cyan

Write-Host "`n✅ Código corrigido (removido createAcl)" -ForegroundColor Green
Write-Host "⏳ Configuração do bucket pendente" -ForegroundColor Yellow
Write-Host "⏳ Teste de funcionamento pendente" -ForegroundColor Yellow

Write-Host "`n=== PRÓXIMOS PASSOS ===" -ForegroundColor Cyan

Write-Host "`n1. Execute o comando IAM no Google Cloud Shell" -ForegroundColor White
Write-Host "2. Reinicie a aplicação" -ForegroundColor White
Write-Host "3. Teste o upload de logo" -ForegroundColor White
Write-Host "4. Verifique se URL funciona no navegador" -ForegroundColor White

Read-Host "`nPressione Enter para sair"


