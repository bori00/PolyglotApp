package com.polyglot.service.student_course_management.exceptions;

/**
 * Exception thrown when a user attempts to access the data of a course to which they do not have
 * access (they are not enrolled in or teaching the course.)
 */
public class InvalidCourseAccessException extends Exception {
}
