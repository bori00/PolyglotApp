package com.polyglot.service.lesson_practice.exceptions;

/**
 * Exception thrown when an exercise for a lesson is requested, but there are no words yet marked
 * as unknown (i.e. "word to learn").
 */
public class NoWordsToLearnException extends Exception {
}
