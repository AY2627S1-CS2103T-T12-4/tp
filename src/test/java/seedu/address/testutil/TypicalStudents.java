package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.student.Student;

/**
 * A utility class containing a list of {@code Student} objects to be used in tests.
 * A {@code Student} is immutable, so these constants can be shared safely between tests.
 */
public class TypicalStudents {

    public static final Student ALICE = new StudentBuilder().withName("Alice Tan")
            .withStudentId("A0123456X").build();
    public static final Student BEN = new StudentBuilder().withName("Ben Lim")
            .withStudentId("A0234567Y").build();
    public static final Student CARL = new StudentBuilder().withName("Carl Goh")
            .withStudentId("A0345678Z").build();
    public static final Student DANIEL = new StudentBuilder().withName("Daniel Ong")
            .withStudentId("A0456789W").build();
    public static final Student ELLE = new StudentBuilder().withName("Elle Teo")
            .withStudentId("A0567890U").build();

    // Manually added, so that they are not in any typical group
    public static final Student FIONA = new StudentBuilder().withName("Fiona Lee")
            .withStudentId("A0678901R").build();
    public static final Student GEORGE = new StudentBuilder().withName("George Yeo")
            .withStudentId("A0789012N").build();

    private TypicalStudents() {} // prevents instantiation

    /**
     * Returns a new list of the typical students, which are not FIONA and GEORGE.
     */
    public static List<Student> getTypicalStudents() {
        return new ArrayList<>(Arrays.asList(ALICE, BEN, CARL, DANIEL, ELLE));
    }
}
