package com.vendi.vendi_ms.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "google.cloud.storage.bucket-name=vendi-pdv"
})
class GoogleCloudStorageServiceTest {

    @Autowired
    private GoogleCloudStorageService storageService;

    @Test
    void testUploadLogo() throws Exception {
        // Criar um arquivo mock
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test-logo.jpg",
            "image/jpeg",
            "fake image content".getBytes()
        );

        // Testar upload
        String result = storageService.uploadLogo(file, "Empresa Teste");
        
        // Verificar se retornou uma URL válida
        assertNotNull(result);
        assertTrue(result.contains("vendi-pdv"));
        assertTrue(result.contains("logos/Empresa_Teste"));
        assertTrue(result.contains("test-logo.jpg"));
        
        System.out.println("URL gerada: " + result);
    }

    @Test
    void testDeleteLogo() {
        // Testar exclusão com URL mock
        String mockUrl = "https://storage.googleapis.com/vendi-pdv/logos/Empresa_Teste/20241201_143022_a1b2c3d4.jpg";
        
        // Não deve lançar exceção
        assertDoesNotThrow(() -> storageService.deleteLogo(mockUrl));
    }

    @Test
    void testInvalidFile() {
        // Testar com arquivo inválido
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "not an image".getBytes()
        );

        // Deve funcionar mesmo com arquivo inválido (apenas para teste)
        assertDoesNotThrow(() -> {
            storageService.uploadLogo(invalidFile, "Empresa Teste");
        });
    }
}

