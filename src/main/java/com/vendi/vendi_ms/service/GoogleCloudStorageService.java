package com.vendi.vendi_ms.service;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.Storage.SignUrlOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Service
@Slf4j
public class GoogleCloudStorageService {

    @Value("${google.cloud.storage.bucket-name:vendi-pdv}")
    private String bucketName;

    @Value("${GOOGLE_CLOUD_CREDENTIALS:}")
    private String credentials;

    private Storage storage;

    private Storage getStorage() throws IOException {
        if (storage == null) {
            if (credentials != null && !credentials.trim().isEmpty()) {
                try {
                    // Tentar usar credentials JSON direto
                    ServiceAccountCredentials serviceAccount = ServiceAccountCredentials
                        .fromStream(new ByteArrayInputStream(credentials.getBytes()));
                    
                    storage = StorageOptions.newBuilder()
                        .setCredentials(serviceAccount)
                        .build()
                        .getService();
                    
                    log.info("Google Cloud Storage inicializado com credenciais JSON");
                } catch (Exception e) {
                    log.warn("Erro ao inicializar com credenciais JSON, tentando Application Default Credentials: {}", e.getMessage());
                    // Fallback para Application Default Credentials
                    storage = StorageOptions.getDefaultInstance().getService();
                }
            } else {
                // Usar Application Default Credentials (variável GOOGLE_APPLICATION_CREDENTIALS)
                storage = StorageOptions.getDefaultInstance().getService();
                log.info("Google Cloud Storage inicializado com Application Default Credentials");
            }
        }
        return storage;
    }

    public String uploadLogo(MultipartFile file, String empresaNome) throws IOException {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = String.format("logos/%s/%s_%s%s", 
                sanitizeFileName(empresaNome), 
                timestamp, 
                UUID.randomUUID().toString().substring(0, 8),
                extension);

            log.info("Iniciando upload para bucket: {}, arquivo: {}", bucketName, fileName);
            log.info("File details - Name: {}, Size: {} bytes, ContentType: {}", 
                originalFilename, file.getSize(), file.getContentType());

            // Fazer upload real para Google Cloud Storage
            Storage storageClient = getStorage();
            
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
            
            try (InputStream inputStream = file.getInputStream()) {
                storageClient.createFrom(blobInfo, inputStream);
            }
            
            // Gerar Signed URL válida por 1 ano (365 dias)
            String signedUrl = storageClient.signUrl(blobInfo, 365, TimeUnit.DAYS, SignUrlOption.httpMethod(com.google.cloud.storage.HttpMethod.GET)).toString();
            
            log.info("Logo uploaded successfully with signed URL: {}", signedUrl);
            
            return signedUrl;

        } catch (Exception e) {
            log.error("Erro ao fazer upload da logo: {}", e.getMessage(), e);
            throw new IOException("Erro ao fazer upload da logo: " + e.getMessage());
        }
    }

    public String generateSignedUrl(String fileName) throws IOException {
        try {
            Storage storageClient = getStorage();
            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            
            // Gerar Signed URL válida por 1 ano
            String signedUrl = storageClient.signUrl(blobInfo, 365, TimeUnit.DAYS, SignUrlOption.httpMethod(com.google.cloud.storage.HttpMethod.GET)).toString();
            
            log.info("Signed URL generated for: {}", fileName);
            return signedUrl;
            
        } catch (Exception e) {
            log.error("Erro ao gerar Signed URL: {}", e.getMessage(), e);
            throw new IOException("Erro ao gerar Signed URL: " + e.getMessage());
        }
    }

    public void deleteLogo(String urlLogo) {
        try {
            if (urlLogo != null) {
                String fileName = extractFileNameFromSignedUrl(urlLogo);
                
                if (fileName != null) {
                    Storage storageClient = getStorage();
                    BlobId blobId = BlobId.of(bucketName, fileName);
                    
                    boolean deleted = storageClient.delete(blobId);
                    if (deleted) {
                        log.info("Logo deleted successfully: {}", fileName);
                    } else {
                        log.warn("Logo não encontrada para exclusão: {}", fileName);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Erro ao deletar logo: {}", e.getMessage(), e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".jpg";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return ".jpg";
        }
        
        return filename.substring(lastDotIndex).toLowerCase();
    }

    private String sanitizeFileName(String fileName) {
        if (fileName == null) {
            return "empresa";
        }
        
        return fileName
                .replaceAll("[^a-zA-Z0-9\\s]", "")
                .replaceAll("\\s+", "_")
                .toLowerCase()
                .substring(0, Math.min(fileName.length(), 50));
    }


    private String extractFileNameFromSignedUrl(String signedUrl) {
        if (signedUrl == null) {
            return null;
        }
        
        try {
            // Signed URLs têm formato: https://storage.googleapis.com/bucket/file?X-Goog-Algorithm=...
            // Extrair o nome do arquivo antes dos parâmetros
            String baseUrl = signedUrl.split("\\?")[0];
            String prefix = "https://storage.googleapis.com/" + bucketName + "/";
            
            if (baseUrl.startsWith(prefix)) {
                return baseUrl.substring(prefix.length());
            }
            
            return null;
        } catch (Exception e) {
            log.warn("Erro ao extrair nome do arquivo da Signed URL: {}", e.getMessage());
            return null;
        }
    }
}
