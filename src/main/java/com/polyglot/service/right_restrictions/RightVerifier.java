package com.polyglot.service.right_restrictions;

import com.polyglot.model.Course;
import com.polyglot.model.Lesson;
import com.polyglot.model.User;

/**
 * Class responsible for verifying whether a user has the right to access or modify certain data.
 */
public class RightVerifier implements IRightVerifier {

    /** {@inheritDoc} */
    @Override
    public boolean hasAccessToTheDataOf(User user, Course course) {
        return RightVerifierFactory.getRightVerifier(user).hasAccessToTheDataOf(user, course);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasAccessToTheDataOf(User user, Lesson lesson) {
        return RightVerifierFactory.getRightVerifier(user).hasAccessToTheDataOf(user, lesson);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasRightToModifyTheDataOf(User user, Course course) {
        return RightVerifierFactory.getRightVerifier(user).hasRightToModifyTheDataOf(user, course);
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasRightToModifyTheDataOf(User user, Lesson lesson) {
        return RightVerifierFactory.getRightVerifier(user).hasRightToModifyTheDataOf(user, lesson);
    }
}
