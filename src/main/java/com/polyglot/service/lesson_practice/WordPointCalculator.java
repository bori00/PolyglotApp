package com.polyglot.service.lesson_practice;

import com.polyglot.model.WordToLearn;

/**
 * Class responsible for computing the points for a WordToLearn.
 */
public class WordPointCalculator {
    /**
     * The value of a good answer.
     */
    public static final int GOOD_ANSWER_POINTS = 1;
    /**
     * The penalty for a wrong answer.
     */
    public static final int BAD_ANSWER_POINTS = -2;

    /**
     * Computes the new amount of collected points, after the user answers a question.
     * @param currentPoints the amount of currently collected points.
     * @param correctAnswer shows whether the new answer of the user was corrcet or not.
     * @return the new amount of collected points.
     */
    public int getNewPoints(int currentPoints, boolean correctAnswer) {
        if (correctAnswer) {
            return currentPoints + GOOD_ANSWER_POINTS;
        } else {
            return currentPoints + BAD_ANSWER_POINTS;
        }
    }
}
