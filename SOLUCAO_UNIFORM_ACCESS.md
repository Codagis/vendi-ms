# 🚨 SOLUÇÃO: Uniform Bucket-Level Access

## ❌ **Problema:**
```
Cannot update access control for an object when uniform bucket-level access is enabled
```

## ✅ **Solução Rápida:**

### **1. Google Cloud Shell (Recomendado)**
```bash
# Acesse: https://shell.cloud.google.com
gcloud config set project ontimeserv
gsutil iam ch allUsers:objectViewer gs://vendi-pdv
```

### **2. Console Web**
1. **Acesse**: https://console.cloud.google.com/storage
2. **Clique no bucket**: `vendi-pdv`
3. **Vá para**: Permissions
4. **Grant Access**:
   - **New principals**: `allUsers`
   - **Role**: `Storage Object Viewer`
5. **Save**

## 🔧 **O que foi corrigido:**

### **Código:**
- ✅ **Removido** `storageClient.createAcl()` (não funciona com Uniform Access)
- ✅ **Mantido** upload normal para o bucket
- ✅ **Permissões** agora gerenciadas via IAM

### **Configuração:**
- ✅ **Uniform Bucket-Level Access** mantido (mais seguro)
- ✅ **Permissões públicas** configuradas via IAM
- ✅ **Acesso** a todos os objetos do bucket

## 🎯 **Como funciona agora:**

1. **Upload**: Arquivo enviado para `gs://vendi-pdv/logos/[empresa]/`
2. **Permissões**: Configuradas no nível do bucket via IAM
3. **Acesso**: Público para todos os objetos do bucket
4. **URL**: Funciona normalmente no navegador

## 🧪 **Teste:**

### **1. Configure as permissões:**
```bash
gsutil iam ch allUsers:objectViewer gs://vendi-pdv
```

### **2. Teste o upload:**
- Acesse a tela de empresas
- Faça upload de uma logo
- Copie a URL gerada
- Abra no navegador

### **3. Verifique no console:**
- Acesse: https://console.cloud.google.com/storage
- Navegue para: `vendi-pdv/logos/`
- Verifique se arquivos estão lá

## 📋 **Comandos Úteis:**

```bash
# Verificar permissões do bucket
gsutil iam get gs://vendi-pdv

# Listar arquivos
gsutil ls -r gs://vendi-pdv/logos/

# Verificar Uniform Bucket-Level Access
gsutil uniformbucketlevelaccess get gs://vendi-pdv

# Remover permissões públicas (se necessário)
gsutil iam ch -d allUsers:objectViewer gs://vendi-pdv
```

## 🔐 **Segurança:**

### **✅ Seguro:**
- Apenas **leitura pública** dos arquivos
- **Upload** ainda requer autenticação
- **Delete** ainda requer autenticação
- **Uniform Bucket-Level Access** mantido

### **⚠️ Considerações:**
- URLs são **públicas** (qualquer um com o link pode ver)
- Para maior segurança, considere usar **signed URLs**
- Ou implementar **proxy** na aplicação

## 🚀 **Status:**

- ✅ **Código corrigido**
- ⏳ **Configuração do bucket pendente**
- ⏳ **Teste pendente**

---

**🎯 Execute o comando IAM e teste o upload!**
