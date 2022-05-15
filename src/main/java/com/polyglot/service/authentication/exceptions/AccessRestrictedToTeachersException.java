package com.polyglot.service.authentication.exceptions;

/**
 * Exception thrown when a user with no teacher rights attempts to perform an operation restricted
 * to teachers only.
 */
public class AccessRestrictedToTeachersException extends Exception {
}
