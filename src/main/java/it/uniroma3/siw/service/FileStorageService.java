package it.uniroma3.siw.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.config.FileStorageProperties;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Path fileStorageLocation;

    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            if (!Files.isWritable(this.fileStorageLocation)) {
                throw new RuntimeException("La directory " + this.fileStorageLocation + " non è scrivibile.");
            }
            logger.info("Directory di upload configurata: {}", this.fileStorageLocation);
        } catch (IOException | SecurityException ex) {
            throw new RuntimeException("Impossibile creare la directory per i file: " + fileStorageProperties.getUploadDir(), ex);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Il file è vuoto.");
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            String filePath = "/Uploads/" + fileName;
            logger.info("File salvato: {}, filePath restituito: {}", targetLocation, filePath);
            return filePath;
        } catch (IOException ex) {
            throw new RuntimeException("Impossibile salvare il file " + fileName, ex);
        }
    }

    public void deleteFile(String filePath) throws IOException, SecurityException {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            logger.info("File {} deleted successfully", filePath);
        } catch (IOException | SecurityException e) {
            logger.error("Failed to delete file {}: {}", filePath, e.getMessage());
            throw e;
        }
    }
}