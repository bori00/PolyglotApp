package com.polyglot.service.lesson_storage;

import com.polyglot.service.lesson_storage.exceptions.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class LessonStorageService {
    private static final String LESSON_PATH = "lessons/lesson%d.pdf";

    private static final Logger logger = LoggerFactory.getLogger(LessonStorageService.class);

    /**
     * Saves a file in the folder of the lessons.
     *
     * @param file     is the file to be saved.
     * @param lessonId is the id of the lesson, whose content is stored in the file. The
     *                 identifier will be part of the file's name.
     * @throws FileStorageException if saving the file fails.
     */
    public void storeLesson(MultipartFile file, Long lessonId) throws FileStorageException {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file.");
            }
            Path destinationFile = Path.of(getLessonFilePath(lessonId));
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("STORAGE UPDATE - saved content of lesson {}", lessonId);
            }
        } catch (IOException e) {
            logger.error("STORAGE FAILURE - saving content of lesson {} failed", lessonId);
            throw new FileStorageException("Failed to store file.", e);
        }
    }

    public byte[] getLessonFile(Long lessonId) throws IOException {
        Path pdfPath = Paths.get(getLessonFilePath(lessonId));
        return Files.readAllBytes(pdfPath);
    }

    private String getLessonFilePath(Long lessonId) {
        return String.format(LESSON_PATH, lessonId);
    }
}
