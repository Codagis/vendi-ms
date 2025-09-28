# Script para configurar bucket público no Google Cloud Storage
# Execute no Google Cloud Shell ou com gcloud CLI instalado

Write-Host "=== Configuração de Bucket Público - Vendi PDV ===" -ForegroundColor Green

Write-Host "`n=== Comandos para Google Cloud Shell ===" -ForegroundColor Cyan

Write-Host "`n1. Configurar projeto:" -ForegroundColor Yellow
Write-Host "gcloud config set project ontimeserv" -ForegroundColor White

Write-Host "`n2. Criar bucket (se não existir):" -ForegroundColor Yellow
Write-Host "gsutil mb gs://vendi-pdv" -ForegroundColor White

Write-Host "`n3. Configurar bucket para acesso público:" -ForegroundColor Yellow
Write-Host "gsutil iam ch allUsers:objectViewer gs://vendi-pdv" -ForegroundColor White

Write-Host "`n4. Verificar permissões:" -ForegroundColor Yellow
Write-Host "gsutil iam get gs://vendi-pdv" -ForegroundColor White

Write-Host "`n=== Alternativa via Console Web ===" -ForegroundColor Cyan

Write-Host "`n1. Acesse: https://console.cloud.google.com/storage" -ForegroundColor White
Write-Host "2. Clique no bucket 'vendi-pdv'" -ForegroundColor White
Write-Host "3. Vá para 'Permissions'" -ForegroundColor White
Write-Host "4. Clique em 'Grant Access'" -ForegroundColor White
Write-Host "5. Adicione: allUsers" -ForegroundColor White
Write-Host "6. Role: Storage Object Viewer" -ForegroundColor White

Write-Host "`n=== Configuração de CORS (se necessário) ===" -ForegroundColor Cyan

Write-Host "`nCrie um arquivo cors.json:" -ForegroundColor Yellow
Write-Host "@"
Write-Host "["
Write-Host "  {"
Write-Host "    ""origin"": [""*""],"
Write-Host "    ""method"": [""GET"", ""POST"", ""PUT"", ""DELETE""],"
Write-Host "    ""responseHeader"": [""Content-Type""],"
Write-Host "    ""maxAgeSeconds"": 3600"
Write-Host "  }"
Write-Host "]"
Write-Host "@"

Write-Host "`nAplique a configuração CORS:" -ForegroundColor Yellow
Write-Host "gsutil cors set cors.json gs://vendi-pdv" -ForegroundColor White

Write-Host "`n=== Teste de Funcionamento ===" -ForegroundColor Cyan

Write-Host "`n1. Faça upload de uma logo na aplicação" -ForegroundColor White
Write-Host "2. Copie a URL gerada" -ForegroundColor White
Write-Host "3. Abra a URL no navegador" -ForegroundColor White
Write-Host "4. A imagem deve carregar normalmente" -ForegroundColor White

Write-Host "`n=== URLs de Teste ===" -ForegroundColor Cyan

Write-Host "`nURLs geradas terão o formato:" -ForegroundColor White
Write-Host "https://storage.googleapis.com/vendi-pdv/logos/[empresa]/[arquivo]" -ForegroundColor Green

Write-Host "`n=== Solução de Problemas ===" -ForegroundColor Cyan

Write-Host "`nSe ainda der erro 'Permission denied':" -ForegroundColor Red
Write-Host "1. Verifique se o bucket existe" -ForegroundColor White
Write-Host "2. Verifique se allUsers tem permissão de leitura" -ForegroundColor White
Write-Host "3. Verifique se o arquivo foi realmente enviado" -ForegroundColor White
Write-Host "4. Teste com gsutil ls gs://vendi-pdv/logos/" -ForegroundColor White

Write-Host "`n=== Comandos Úteis ===" -ForegroundColor Cyan

Write-Host "`nListar arquivos no bucket:" -ForegroundColor White
Write-Host "gsutil ls -r gs://vendi-pdv/logos/" -ForegroundColor Green

Write-Host "`nVerificar permissões de um arquivo:" -ForegroundColor White
Write-Host "gsutil acl get gs://vendi-pdv/logos/[arquivo]" -ForegroundColor Green

Write-Host "`nRemover permissões públicas (se necessário):" -ForegroundColor White
Write-Host "gsutil iam ch -d allUsers:objectViewer gs://vendi-pdv" -ForegroundColor Green

Read-Host "`nPressione Enter para sair"


