# 🚀 Configuração Simplificada - Vendi PDV

## 📁 Estrutura de Ambientes

```
vendi-ms/
└── src/main/resources/
    ├── application.yml          # Configuração base
    ├── application-dev.yml      # Desenvolvimento
    ├── application-homolog.yml  # Homologação
    └── application-prod.yml     # Produção
```

## 🔧 Configuração Local

### Opção 1: Script Automático
```powershell
# Execute como administrador
.\setup-env.ps1
```

### Opção 2: Manual
```powershell
setx GOOGLE_CLOUD_CREDENTIALS "C:\caminho\para\seu\arquivo.json"
setx GOOGLE_CLOUD_BUCKET_NAME "vendi-pdv"
```

## 🚀 Railway (Produção)

### Configuração no Dashboard
```bash
GOOGLE_CLOUD_CREDENTIALS={"type":"service_account","project_id":"seu-projeto-id",...}
GOOGLE_CLOUD_BUCKET_NAME=vendi-pdv
```

## 🏃‍♂️ Como Executar

### Desenvolvimento
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Homologação
```bash
mvn spring-boot:run -Dspring.profiles.active=homolog
```

### Produção
```bash
mvn spring-boot:run -Dspring.profiles.active=prod
```

## 🔐 Variáveis de Ambiente

### Únicas e Simples
- `GOOGLE_CLOUD_CREDENTIALS` - Caminho do arquivo JSON (local) ou conteúdo JSON (produção)
- `GOOGLE_CLOUD_BUCKET_NAME` - Nome do bucket (padrão: vendi-pdv)

### Opcionais
- `DATABASE_URL` - URL do banco de dados
- `DB_USERNAME` - Usuário do banco
- `DB_PASSWORD` - Senha do banco
- `JWT_SECRET` - Chave secreta JWT

## 📋 Checklist

### Desenvolvimento:
- [ ] Service Account criada
- [ ] Arquivo JSON baixado
- [ ] Script executado: `.\setup-env.ps1`
- [ ] Terminal reiniciado
- [ ] Teste: `mvn spring-boot:run -Dspring.profiles.active=dev`

### Railway:
- [ ] Variáveis configuradas no dashboard
- [ ] Deploy realizado
- [ ] Teste de upload funcionando

## 🎯 Resumo

✅ **Uma única variável**: `GOOGLE_CLOUD_CREDENTIALS`  
✅ **Três ambientes**: dev, homolog, prod  
✅ **Configuração simples**: Script automático  
✅ **Funciona local e produção**: Mesma variável, diferentes valores  

---

**🚀 Configuração simplificada e funcionando!**
