package seedu.address.model.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class AssignmentRecordTest {
    @Test
    public void constructor_validatesSubmissionAndGrade() {
        assertEquals(Optional.empty(), AssignmentRecord.notSubmitted().getGrade());
        assertThrows(NullPointerException.class, () -> new AssignmentRecord(true, null));
        assertThrows(IllegalArgumentException.class, () -> new AssignmentRecord(false, Optional.of(50)));
        for (int grade : new int[] {-1, 101}) {
            assertThrows(IllegalArgumentException.class,
                    AssignmentRecord.MESSAGE_INVALID_GRADE, () -> new AssignmentRecord(true, Optional.of(grade)));
        }
        assertEquals(Optional.of(0), new AssignmentRecord(true, Optional.of(0)).getGrade());
        assertEquals(Optional.of(100), new AssignmentRecord(true, Optional.of(100)).getGrade());
    }

    @Test
    public void withSubmissionStatus_updatesStatusAndClearsGradeWhenWithdrawn() {
        AssignmentRecord graded = new AssignmentRecord(true, Optional.of(87));
        assertEquals(graded, graded.withSubmissionStatus(true));
        AssignmentRecord withdrawn = graded.withSubmissionStatus(false);
        assertEquals(AssignmentRecord.notSubmitted(), withdrawn);
        assertEquals(Optional.empty(), withdrawn.getGrade());
    }

    @Test
    public void withGrade_submittedRecord_updatesGrade() {
        AssignmentRecord submitted = AssignmentRecord.notSubmitted().withSubmissionStatus(true);
        assertEquals(Optional.of(87), submitted.withGrade(87).getGrade());
        assertThrows(IllegalArgumentException.class, "Cannot grade an unsubmitted assignment.", () ->
                AssignmentRecord.notSubmitted().withGrade(87));
        assertThrows(IllegalArgumentException.class, AssignmentRecord.MESSAGE_INVALID_GRADE, () ->
                submitted.withGrade(101));
    }

    @Test
    public void equals_sameValues_matchesAndDifferentValuesDoNot() {
        AssignmentRecord submitted = new AssignmentRecord(true, Optional.of(87));
        assertEquals(submitted, submitted);
        assertEquals(submitted, new AssignmentRecord(true, Optional.of(87)));
        assertEquals(submitted.hashCode(), new AssignmentRecord(true, Optional.of(87)).hashCode());
        assertNotEquals(submitted, AssignmentRecord.notSubmitted());
        assertNotEquals(submitted, null);
        assertNotEquals(submitted, "submitted");
    }
}
