package com.polyglot.controller;

import com.polyglot.service.authentication.exceptions.AccessRestrictedToStudentsException;
import com.polyglot.service.authentication.exceptions.AccessRestrictedToTeachersException;
import com.polyglot.service.authentication.exceptions.AuthenticationRequiredException;
import com.polyglot.service.authentication.exceptions.DuplicateUsernameException;
import com.polyglot.service.lesson_practice.exceptions.NoWordsToLearnException;
import com.polyglot.service.student_course_management.exceptions.InvalidCourseAccessException;
import com.polyglot.service.student_course_management.exceptions.LanguageNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class responsible for returning the corresponding ExceptionResponse, whenever an unhandled
 * exception is thrown in the handler method of a controller.
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @Getter
    @AllArgsConstructor
    public static class ExceptionResponse {
        private final List<String> messages;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ExceptionResponse handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return new ExceptionResponse(
                ex.getBindingResult()
                        .getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.toList()));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)  // 403
    @ExceptionHandler(DuplicateUsernameException.class)
    public @ResponseBody
    ExceptionResponse handleDuplicateUsernameException(
            Exception ex) {
        return new ExceptionResponse(List.of("This username is already taken. Please specify " +
                "another one!"));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401
    @ExceptionHandler(AuthenticationRequiredException.class)
    public @ResponseBody
    ExceptionResponse handleAuthenticationRequiredException(
            Exception ex) {
        return new ExceptionResponse(List.of("You must be authenticated to access this " +
                "functionality!"));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401
    @ExceptionHandler(AccessRestrictedToTeachersException.class)
    public @ResponseBody
    ExceptionResponse handleAccessRestrictedToTeachersException(
            Exception ex) {
        return new ExceptionResponse(List.of("You must be a Teacher to access this " +
                "functionality!"));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401
    @ExceptionHandler(AccessRestrictedToStudentsException.class)
    public @ResponseBody
    ExceptionResponse handleAccessRestrictedToStudentsException(
            Exception ex) {
        return new ExceptionResponse(List.of("You must be a Student to access this " +
                "functionality!"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(LanguageNotFoundException.class)
    public @ResponseBody
    ExceptionResponse handleLanguageNotFoundException(
            Exception ex) {
        return new ExceptionResponse(List.of("This language is not supported!"));
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)  // 401
    @ExceptionHandler(InvalidCourseAccessException.class)
    public @ResponseBody
    ExceptionResponse handleInvalidCourseAccessException(
            Exception ex) {
        return new ExceptionResponse(List.of("You do not have access to this course's data"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(NoWordsToLearnException.class)
    public @ResponseBody
    ExceptionResponse handleNoWordsToLearnException(
            Exception ex) {
        return new ExceptionResponse(List.of("No exercise can be generated, because you have not " +
                "marked any words as 'unknown' yet. Please add unknown words for this lesson " +
                "before generating an exercise."));
    }
}
