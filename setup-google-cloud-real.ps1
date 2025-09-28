# Script para configurar Google Cloud Storage real
# Execute como administrador

Write-Host "=== Configuração Google Cloud Storage - Vendi PDV ===" -ForegroundColor Green

# Verificar se o arquivo JSON existe
$jsonPath = Read-Host "Digite o caminho completo para o arquivo JSON das credenciais"
if (-not (Test-Path $jsonPath)) {
    Write-Host "❌ Arquivo não encontrado: $jsonPath" -ForegroundColor Red
    exit 1
}

# Ler o conteúdo do JSON
try {
    $jsonContent = Get-Content $jsonPath -Raw -Encoding UTF8
    $jsonObject = $jsonContent | ConvertFrom-Json
    
    Write-Host "✅ Arquivo JSON válido encontrado" -ForegroundColor Green
    Write-Host "Project ID: $($jsonObject.project_id)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Arquivo JSON inválido: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Configurar variáveis de ambiente
Write-Host "`nConfigurando variáveis de ambiente..." -ForegroundColor Yellow

try {
    # Configurar GOOGLE_APPLICATION_CREDENTIALS (caminho do arquivo)
    [Environment]::SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", $jsonPath, "User")
    
    # Configurar GOOGLE_CLOUD_CREDENTIALS (conteúdo JSON)
    [Environment]::SetEnvironmentVariable("GOOGLE_CLOUD_CREDENTIALS", $jsonContent, "User")
    
    # Configurar bucket name
    [Environment]::SetEnvironmentVariable("GOOGLE_CLOUD_BUCKET_NAME", "vendi-pdv", "User")
    
    Write-Host "✅ Variáveis configuradas com sucesso!" -ForegroundColor Green
} catch {
    Write-Host "❌ Erro ao configurar variáveis: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Verificar configuração
Write-Host "`n=== Verificação ===" -ForegroundColor Cyan

$envVars = @(
    @{Name="GOOGLE_APPLICATION_CREDENTIALS"; Description="Caminho do arquivo JSON"},
    @{Name="GOOGLE_CLOUD_CREDENTIALS"; Description="Conteúdo JSON (primeiros 50 chars)"},
    @{Name="GOOGLE_CLOUD_BUCKET_NAME"; Description="Nome do Bucket"}
)

foreach ($var in $envVars) {
    $value = [Environment]::GetEnvironmentVariable($var.Name, "User")
    if ($value) {
        if ($var.Name -eq "GOOGLE_CLOUD_CREDENTIALS") {
            $displayValue = $value.Substring(0, [Math]::Min(50, $value.Length)) + "..."
        } else {
            $displayValue = $value
        }
        Write-Host "✅ $($var.Name): $displayValue" -ForegroundColor Green
    } else {
        Write-Host "❌ $($var.Name): Não configurada" -ForegroundColor Red
    }
}

Write-Host "`n=== Configuração Concluída ===" -ForegroundColor Green
Write-Host "Próximos passos:" -ForegroundColor Yellow
Write-Host "1. Reinicie o terminal/IDE" -ForegroundColor White
Write-Host "2. Execute: mvn spring-boot:run -Dspring.profiles.active=dev" -ForegroundColor White
Write-Host "3. Teste o upload de logo na interface" -ForegroundColor White

Write-Host "`n=== Informações Importantes ===" -ForegroundColor Cyan
Write-Host "• Service Account precisa ter permissão 'Storage Object Admin'" -ForegroundColor White
Write-Host "• Bucket 'vendi-pdv' deve existir no projeto Google Cloud" -ForegroundColor White
Write-Host "• Arquivos serão salvos em: logos/[nome-empresa]/" -ForegroundColor White

Write-Host "`n=== Teste Manual ===" -ForegroundColor Cyan
Write-Host "1. Acesse: https://console.cloud.google.com/storage" -ForegroundColor White
Write-Host "2. Verifique se o bucket 'vendi-pdv' existe" -ForegroundColor White
Write-Host "3. Verifique as permissões da Service Account" -ForegroundColor White

Read-Host "`nPressione Enter para sair"


