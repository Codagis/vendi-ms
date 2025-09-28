# Script simplificado para configurar variáveis de ambiente
# Execute como administrador

Write-Host "=== Configuração Simplificada - Vendi PDV ===" -ForegroundColor Green

# Solicitar informações
Write-Host "`n=== Google Cloud Storage ===" -ForegroundColor Cyan

$credentialsPath = Read-Host "Digite o caminho completo para o arquivo JSON das credenciais"
if (-not (Test-Path $credentialsPath)) {
    Write-Host "❌ Arquivo não encontrado: $credentialsPath" -ForegroundColor Red
    exit 1
}

$bucketName = Read-Host "Digite o nome do bucket (padrão: vendi-pdv)"
if ([string]::IsNullOrWhiteSpace($bucketName)) {
    $bucketName = "vendi-pdv"
}

# Configurar variáveis de ambiente
Write-Host "`nConfigurando variáveis de ambiente..." -ForegroundColor Yellow

try {
    [Environment]::SetEnvironmentVariable("GOOGLE_CLOUD_CREDENTIALS", $credentialsPath, "User")
    [Environment]::SetEnvironmentVariable("GOOGLE_CLOUD_BUCKET_NAME", $bucketName, "User")
    Write-Host "✅ Variáveis configuradas com sucesso!" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro ao configurar variáveis: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Verificar configuração
Write-Host "`n=== Verificação ===" -ForegroundColor Cyan

$envVars = @(
    @{Name="GOOGLE_CLOUD_CREDENTIALS"; Description="Credenciais Google Cloud"},
    @{Name="GOOGLE_CLOUD_BUCKET_NAME"; Description="Nome do Bucket"}
)

foreach ($var in $envVars) {
    $value = [Environment]::GetEnvironmentVariable($var.Name, "User")
    if ($value) {
        Write-Host "✅ $($var.Name): $value" -ForegroundColor Green
    } else {
        Write-Host "❌ $($var.Name): Não configurada" -ForegroundColor Red
    }
}

Write-Host "`n=== Configuração Concluída ===" -ForegroundColor Green
Write-Host "Próximos passos:" -ForegroundColor Yellow
Write-Host "1. Reinicie o terminal/IDE" -ForegroundColor White
Write-Host "2. Execute: mvn spring-boot:run -Dspring.profiles.active=dev" -ForegroundColor White
Write-Host "3. Teste o upload de logo na interface" -ForegroundColor White

Write-Host "`n=== Ambientes Disponíveis ===" -ForegroundColor Cyan
Write-Host "• dev - Desenvolvimento local" -ForegroundColor White
Write-Host "• homolog - Homologação" -ForegroundColor White
Write-Host "• prod - Produção" -ForegroundColor White

Write-Host "`n=== Informações Importantes ===" -ForegroundColor Cyan
Write-Host "• Variável única: GOOGLE_CLOUD_CREDENTIALS" -ForegroundColor White
Write-Host "• Bucket: $bucketName" -ForegroundColor White
Write-Host "• Funciona em todos os ambientes" -ForegroundColor White

Read-Host "`nPressione Enter para sair"


