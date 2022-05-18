package com.polyglot.service.right_restrictions;

import com.polyglot.model.Student;
import com.polyglot.model.Teacher;
import com.polyglot.model.User;
import com.polyglot.service.right_restrictions.strategies.StudentRightVerifierStrategy;
import com.polyglot.service.right_restrictions.strategies.TeacherRightVerifierStrategy;

/**
 * Constructs the RightVerifier corresponding to the type of a user.
 */
public class RightVerifierFactory {

    /**
     * Constructs the RightVerifier corresponding to the type of a user.
     * @param user is the user to whose type the verifier is adjusted.
     * @return the constructed RightVerifier.
     */
    public static IRightVerifier getRightVerifier(User user) {
        if (user instanceof Student) {
            return new StudentRightVerifierStrategy();
        }
        if (user instanceof Teacher) {
            return new TeacherRightVerifierStrategy();
        }
        throw new IllegalArgumentException("Unknown user type");
    }
}
