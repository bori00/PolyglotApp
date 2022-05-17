package com.polyglot.service.student_course_management;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.testing.RemoteTranslateHelper;
import com.polyglot.model.*;
import com.polyglot.model.DTO.EnrolledCourseDTO;
import com.polyglot.model.DTO.ExtendedEnrolledCourseDTO;
import com.polyglot.model.DTO.SelfTaughtCourseDTO;
import com.polyglot.repository.*;
import com.polyglot.service.authentication.AuthenticationService;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.file_storage.FileStorageService;
import com.polyglot.service.file_storage.exceptions.FileStorageException;
import com.polyglot.service.student_course_management.exceptions.CourseNotFoundException;
import com.polyglot.service.student_course_management.exceptions.InvalidCourseAccessException;
import com.polyglot.service.student_course_management.exceptions.LanguageNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class responsible for actions related to the management of self-taught courses.
 */
@Service
public class StudentCourseManagementService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private LanguageRepository languageRepository;

    @Autowired
    private SelfTaughtCourseRepository selfTaughtCourseRepository;

    @Autowired
    private SelfTaughtLessonRepository selfTaughtLessonRepository;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private WordToLearnRepository wordToLearnRepository;

    @Autowired
    private FileStorageService fileStorageService;

    private static final Logger logger = LoggerFactory.getLogger(StudentCourseManagementService.class);

    private static Translate translate;


    /**
     * Creates and saves in the database a new self-taught course.
     * @param selfTaughtCourseDTO holds the data of the new course.
     * @return the saved course.
     * @throws AccessRestrictedToStudentsException if the current user is not a student.
     * @throws LanguageNotFoundException if the requested language is not supported.
     */
    public SelfTaughtCourse createSelfTaughtCourse(SelfTaughtCourseDTO selfTaughtCourseDTO) throws AccessRestrictedToStudentsException, LanguageNotFoundException {
        Student student = authenticationService.getCurrentStudent();

        Optional<Language> optLanguage =
                languageRepository.findByName(selfTaughtCourseDTO.getLanguage());

        if (optLanguage.isEmpty()) {
            logger.warn(String.format("INVALID UPDATE - user %s attempted to create a course for " +
                            "not supported language %s",
                    student.getUserName(), selfTaughtCourseDTO.getLanguage()));
            throw new LanguageNotFoundException();
        }

        SelfTaughtCourse selfTaughtCourse = new SelfTaughtCourse(selfTaughtCourseDTO.getTitle(),
                selfTaughtCourseDTO.getMinPointsPerWord(),
                optLanguage.get(),
                student);
        selfTaughtCourse.addCourseEnrollment(student);

        SelfTaughtCourse savedCourse = selfTaughtCourseRepository.save(selfTaughtCourse);

        logger.info("UPDATE - created new course {}", savedCourse.toString());

        return savedCourse;
    }


    /**
     * Finds and returns all courses, self-taught or supervised, in which the current student is
     * enrolled.
     * @return the courses in which the current student is enrolled.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     */
    public List<EnrolledCourseDTO> getAllEnrolledCourses() throws AccessRestrictedToStudentsException {
        Student student = authenticationService.getCurrentStudent();

        return courseEnrollmentRepository.findByStudent(student)
                .stream()
                .map(CourseEnrollment::getCourse)
                .map(course -> {
                    if (course.getSupervisor().getUserName().equals(student.getUserName())) {
                        return new EnrolledCourseDTO(course.getId(), course.getTitle(),
                                course.getLanguage().getName(), null);
                    } else {
                        return new EnrolledCourseDTO(course.getId(), course.getTitle(),
                                course.getLanguage().getName(),
                                course.getSupervisor().getUserName());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Saves a new self-taught lesson.
     * @param courseId is the identifier of the course to which the lesson belongs.
     * @param title is the title of the new lesson.
     * @param file is the file with the lesson's content.
     * @return the newly saved lesson.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     * @throws CourseNotFoundException if no course with the requested id exists in the database.
     * @throws FileStorageException if saving the file fails.
     */
    public SelfTaughtLesson saveNewSelfTaughtLesson(Long courseId, String title, MultipartFile file) throws AccessRestrictedToStudentsException, CourseNotFoundException, FileStorageException {
        Student student = authenticationService.getCurrentStudent();

        Optional<SelfTaughtCourse> course = selfTaughtCourseRepository.findById(courseId);

        if (course.isEmpty()) {
            logger.warn("INVALID UPDATE = attempt to add a lesson to a course {} that does not " +
                    "exist", courseId);
            throw new CourseNotFoundException();
        }

        SelfTaughtLesson selfTaughtLesson = new SelfTaughtLesson(title,
                course.get().getLessons().size()+1, course.get());

        SelfTaughtLesson savedLesson = selfTaughtLessonRepository.save(selfTaughtLesson);

        logger.info("UPDATE - saved new lesson {} with title {}", savedLesson.getId(),
                savedLesson.getTitle());

        fileStorageService.storeLesson(file, savedLesson.getId());

        return savedLesson;
    }

    /**
     * Returns the data of a course in which the active user, a student, is enrolled.
     * @param courseId is the id of the course whose data is requested and returned.
     * @return the data of the requested course.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     * @throws InvalidCourseAccessException
     */
    public ExtendedEnrolledCourseDTO getEnrolledCourseData(Long courseId) throws AccessRestrictedToStudentsException, InvalidCourseAccessException {
        Student student = authenticationService.getCurrentStudent();

        Course course = courseRepository.getById(courseId);

        if (!course.getEnrollments().stream().anyMatch(courseEnrollment -> courseEnrollment.getStudent().equals(student))) {
            logger.warn("INVALID ACCESS = attempt to access data of course {} by student {}, who " +
                            "is not enrolled",
                    courseId, student);
            throw new InvalidCourseAccessException();
        }

        String teacherName = null;
        if (!course.getSupervisor().getUserName().equals(student.getUserName())) {
            teacherName = course.getSupervisor().getUserName();
        }

        return new ExtendedEnrolledCourseDTO(course.getId(), course.getTitle(),
                course.getLanguage().getName(), teacherName,
                course.getLessons().stream().collect(Collectors.toMap(Lesson::getId,
                        Lesson::getTitle)));
    }

    /**
     * Saves a new word to learn for the active user, to the given lesson, with the translation
     * to the users native language.
     * @param lessonId is the lesson to which the unknown word belongs.
     * @param word is the word to learn.
     * @throws AccessRestrictedToStudentsException if the active user is not a student.
     * @throws InvalidCourseAccessException if the active user is nt enrolled in the course to
     * which the lesson belongs.
     */
    public void saveUnknownWord(Long lessonId, String word) throws AccessRestrictedToStudentsException, InvalidCourseAccessException {
        Student student = authenticationService.getCurrentStudent();

        Lesson lesson = lessonRepository.getById(lessonId);

        Optional<CourseEnrollment> courseEnrollment =
                courseEnrollmentRepository.findByCourseAndStudent(lesson.getCourse(), student);

        if (courseEnrollment.isEmpty()) {
            throw new InvalidCourseAccessException();
        }

        RemoteTranslateHelper helper = RemoteTranslateHelper.create();
        translate = helper.getOptions().getService();

        String translatedWord = word;
        if (!lesson.getCourse().getLanguage().equals(student.getNativeLanguage())) {
            translatedWord = translate.translate(word,
                    Translate.TranslateOption.sourceLanguage(lesson.getCourse().getLanguage().getAPI_ID()),
                    Translate.TranslateOption.targetLanguage(student.getNativeLanguage().getAPI_ID())).getTranslatedText().toLowerCase(Locale.ROOT);
        }

        logger.info("Translated {} in {} to {} in {}", word, lesson.getCourse().getLanguage(),
                translatedWord, student.getNativeLanguage());

        WordToLearn wordToLearn = new WordToLearn(word, translatedWord, 0, courseEnrollment.get()
                , lesson);

        wordToLearnRepository.save(wordToLearn);

        logger.info("UPDATE - saved new word to learn for user {}: {} in {} translated to {} in {}",
                student.getUserName(),
                word,
                lesson.getCourse().getLanguage(),
                translatedWord, student.getNativeLanguage());
    }
}
