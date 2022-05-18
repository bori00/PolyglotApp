package com.polyglot.service.right_restrictions.strategies;

import com.polyglot.model.Course;
import com.polyglot.model.Lesson;
import com.polyglot.model.User;
import com.polyglot.service.right_restrictions.IRightVerifier;

/**
 * Class responsible for verifying whether a teacher has the right to access or modify certain data.
 */
public class TeacherRightVerifierStrategy implements IRightVerifier {

    /** {@inheritDoc} */
    @Override
    public boolean hasAccessToTheDataOf(User user, Course course) {
        return course.getSupervisor().equals(user);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasAccessToTheDataOf(User user, Lesson lesson) {
        return hasAccessToTheDataOf(user, lesson.getCourse());
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasRightToModifyTheDataOf(User user, Course course) {
        return course.getSupervisor().equals(user);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasRightToModifyTheDataOf(User user, Lesson lesson) {
        return hasRightToModifyTheDataOf(user, lesson.getCourse());
    }
}
