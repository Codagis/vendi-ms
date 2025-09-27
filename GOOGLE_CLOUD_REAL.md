# 🚀 Google Cloud Storage - Configuração Real

## 📋 Pré-requisitos

### 1. **Criar Bucket no Google Cloud**
```bash
# Nome do bucket: vendi-pdv
# Localização: us-central1 (ou sua preferência)
# Classe de armazenamento: Standard
# Controle de acesso: Fine-grained
```

### 2. **Criar Service Account**
```bash
# Nome: vendi-storage-service
# Papel: Storage Object Admin
# Descrição: Serviço para upload de logos das empresas
```

### 3. **Baixar Chave JSON**
```bash
# Salvar como: ontimeserv-84963d112fa9.json
# Local seguro: C:\Users\lorde\.vendi\credentials\
```

## 🔧 Configuração Local

### Opção 1: Script Automático
```powershell
# Execute como administrador
.\setup-google-cloud-real.ps1
```

### Opção 2: Manual
```powershell
# Configurar variáveis de ambiente
setx GOOGLE_APPLICATION_CREDENTIALS "C:\Users\lorde\.vendi\credentials\ontimeserv-84963d112fa9.json"
setx GOOGLE_CLOUD_BUCKET_NAME "vendi-pdv"

# Para Railway (conteúdo JSON)
setx GOOGLE_CLOUD_CREDENTIALS '{"type":"service_account","project_id":"seu-projeto",...}'
```

## 🚀 Railway (Produção)

### Variáveis no Dashboard
```bash
GOOGLE_CLOUD_CREDENTIALS={"type":"service_account","project_id":"ontimeserv",...}
GOOGLE_CLOUD_BUCKET_NAME=vendi-pdv
```

## 🧪 Teste de Funcionamento

### 1. **Verificar Logs**
```bash
# Procurar por:
# "Google Cloud Storage inicializado com credenciais JSON"
# "Logo uploaded successfully: https://storage.googleapis.com/..."
```

### 2. **Verificar Bucket**
```bash
# Acessar: https://console.cloud.google.com/storage
# Navegar para: vendi-pdv/logos/
# Verificar se arquivos foram criados
```

### 3. **Testar Upload**
```bash
# 1. Acessar tela de empresas
# 2. Criar/editar empresa
# 3. Fazer upload de logo
# 4. Verificar se URL funciona
```

## 🔐 Permissões Necessárias

### Service Account deve ter:
- ✅ **Storage Object Admin** - Para criar/deletar objetos
- ✅ **Storage Object Viewer** - Para visualizar objetos
- ✅ **Storage Legacy Bucket Reader** - Para acessar bucket

### Bucket deve ter:
- ✅ **Controle de acesso fine-grained** habilitado
- ✅ **Service Account** adicionado com permissões

## 📁 Estrutura de Arquivos

```
vendi-pdv/
└── logos/
    ├── empresa_exemplo_1/
    │   ├── 20241227_143022_a1b2c3d4.jpg
    │   └── 20241227_150000_e5f6g7h8.png
    └── empresa_exemplo_2/
        └── 20241227_160000_i9j0k1l2.jpg
```

## 🚨 Solução de Problemas

### Erro: "Permission denied"
```bash
# Verificar se Service Account tem permissões corretas
# Verificar se bucket existe
# Verificar se credenciais estão corretas
```

### Erro: "Bucket not found"
```bash
# Verificar nome do bucket: vendi-pdv
# Verificar projeto correto
# Verificar região do bucket
```

### Erro: "Invalid credentials"
```bash
# Verificar se arquivo JSON está correto
# Verificar se GOOGLE_APPLICATION_CREDENTIALS aponta para arquivo
# Verificar se Service Account está ativa
```

## 🎯 URLs Geradas

### Formato da URL:
```
https://storage.googleapis.com/vendi-pdv/logos/[empresa]/[timestamp]_[uuid].[ext]
```

### Exemplo:
```
https://storage.googleapis.com/vendi-pdv/logos/empresa_exemplo/20241227_143022_a1b2c3d4.jpg
```

## ✅ Checklist de Configuração

### Local:
- [ ] Bucket `vendi-pdv` criado
- [ ] Service Account criada com permissões
- [ ] Arquivo JSON baixado
- [ ] Script executado: `.\setup-google-cloud-real.ps1`
- [ ] Terminal reiniciado
- [ ] Teste realizado

### Railway:
- [ ] Variáveis configuradas no dashboard
- [ ] Deploy realizado
- [ ] Teste de upload funcionando
- [ ] URLs acessíveis

---

**🚀 Google Cloud Storage configurado e funcionando!**

