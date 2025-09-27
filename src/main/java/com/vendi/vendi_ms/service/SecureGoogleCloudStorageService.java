package com.vendi.vendi_ms.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

@Service
@Slf4j
public class SecureGoogleCloudStorageService {

    @Value("${google.cloud.storage.bucket-name:vendi-pdv}")
    private String bucketName;

    @Value("${google.cloud.storage.credentials-json:}")
    private String credentialsJson;

    private Storage storage;

    private Storage getStorage() {
        if (storage == null) {
            try {
                if (!credentialsJson.isEmpty()) {
                    // Usar credenciais do JSON string (Railway/Produção)
                    ServiceAccountCredentials credentials = ServiceAccountCredentials
                        .fromStream(new java.io.ByteArrayInputStream(credentialsJson.getBytes()));
                    
                    storage = StorageOptions.newBuilder()
                        .setCredentials(credentials)
                        .build()
                        .getService();
                    
                    log.info("Storage inicializado com credenciais JSON");
                } else {
                    // Usar credenciais padrão (desenvolvimento local)
                    storage = StorageOptions.getDefaultInstance().getService();
                    log.info("Storage inicializado com credenciais padrão");
                }
            } catch (Exception e) {
                log.error("Erro ao inicializar storage: {}", e.getMessage());
                throw new RuntimeException("Erro ao configurar Google Cloud Storage", e);
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

            BlobId blobId = BlobId.of(bucketName, fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();

            Blob blob = getStorage().create(blobInfo, file.getBytes());

            String publicUrl = String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
            
            log.info("Logo uploaded successfully: {}", publicUrl);
            return publicUrl;

        } catch (Exception e) {
            log.error("Erro ao fazer upload da logo: {}", e.getMessage(), e);
            throw new IOException("Erro ao fazer upload da logo: " + e.getMessage());
        }
    }

    public void deleteLogo(String urlLogo) {
        try {
            if (urlLogo != null && urlLogo.contains(bucketName)) {
                String fileName = extractFileNameFromUrl(urlLogo);
                BlobId blobId = BlobId.of(bucketName, fileName);
                
                boolean deleted = getStorage().delete(blobId);
                if (deleted) {
                    log.info("Logo deleted successfully: {}", fileName);
                } else {
                    log.warn("Logo not found or already deleted: {}", fileName);
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

    private String extractFileNameFromUrl(String url) {
        if (url == null || !url.contains(bucketName)) {
            return null;
        }
        
        String prefix = "https://storage.googleapis.com/" + bucketName + "/";
        if (url.startsWith(prefix)) {
            return url.substring(prefix.length());
        }
        
        return null;
    }
}

