package com.polyglot.service.file_storage;

import com.polyglot.service.file_storage.exceptions.FileStorageException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Service responsible for storing files for different purposes.
 */
@Service
public class FileStorageService {
    private final String LESSONS_FOLDER = "lessons";

    public void storeLesson(MultipartFile file, Long lessonId) throws FileStorageException {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file.");
            }
            Path destinationFile = Path.of(String.format("%s/lesson%d.pdf", LESSONS_FOLDER, lessonId));
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new FileStorageException("Failed to store file.", e);
        }
    }
}
