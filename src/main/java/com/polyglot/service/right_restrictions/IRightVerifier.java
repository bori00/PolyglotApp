package com.polyglot.service.right_restrictions;

import com.polyglot.model.Course;
import com.polyglot.model.Lesson;
import com.polyglot.model.User;

/**
 * Interface responsible for verifying whether a user has the right to access or modify certain
 * data.
 */
public interface IRightVerifier {

    /**
     * Tells whether a user can access the data related to a certain course.
     * @param user is the user whose right is verified.
     * @param course is the course to which the access is verified.
     * @return true if and only if the user has the right to access the data of the given course.
     */
    boolean hasAccessToTheDataOf(User user, Course course);

    /**
     * Tells whether a user can access the data related to a certain lesson.
     * @param user is the user whose right is verified.
     * @param lesson is the lesson to which the access is verified.
     * @return true if and only if the user has the right to access the data of the given lesson.
     */
    boolean hasAccessToTheDataOf(User user, Lesson lesson);

    /**
     * Tells whether a user can modify the data related to a certain course.
     * @param user is the user whose right is verified.
     * @param course is the course to which the access is verified.
     * @return true if and only if the user has the right to modify the data of the given course.
     */
    boolean hasRightToModifyTheDataOf(User user, Course course);

    /**
     * Tells whether a user can modify the data related to a certain lesson.
     * @param user is the user whose right is verified.
     * @param lesson is the lesson to which the access is verified.
     * @return true if and only if the user has the right to modify the data of the given lesson.
     */
    boolean hasRightToModifyTheDataOf(User user, Lesson lesson);
}
