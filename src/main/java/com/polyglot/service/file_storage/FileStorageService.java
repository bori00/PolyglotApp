package com.polyglot.service.file_storage;

import com.polyglot.service.file_storage.exceptions.FileStorageException;
import com.polyglot.service.student_course_management.StudentCourseManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Service responsible for storing files for different purposes.
 */
@Service
public class FileStorageService {
    private final String LESSON_PATH = "lessons/lesson%d.pdf";

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    /**
     * Saves a file in the folder of the lessons.
     * @param file is the file to be saved.
     * @param lessonId is the id of the lesson, whose content is stored in the file. The
     *                 identifier will be part of the file's name.
     * @throws FileStorageException if saving the file fails.
     */
    public void storeLesson(MultipartFile file, Long lessonId) throws FileStorageException {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file.");
            }
            Path destinationFile = Path.of(String.format(LESSON_PATH, lessonId));
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
                logger.info("STORAGE UPDATE - saved content of lesson {}", lessonId);
            }
        }
        catch (IOException e) {
            logger.error("STORAGE FAILURE - saving content of lesson {} failed", lessonId);
            throw new FileStorageException("Failed to store file.", e);
        }
    }

    public byte[] getLessonFile(Long lessonId) throws IOException {
        Path pdfPath = Paths.get(String.format(LESSON_PATH, lessonId));
        return Files.readAllBytes(pdfPath);
    }
}
