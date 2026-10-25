package seedu.address.model.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;
import static seedu.address.testutil.TypicalAssignments.PS1;
import static seedu.address.testutil.TypicalAssignments.PS2;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.assignment.exceptions.AssignmentNotFoundException;
import seedu.address.model.assignment.exceptions.DuplicateAssignmentException;
import seedu.address.model.student.StudentId;

public class UniqueAssignmentListTest {

    private final UniqueAssignmentList uniqueAssignmentList = new UniqueAssignmentList();

    @Test
    public void contains_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueAssignmentList.contains((Assignment) null));
        assertThrows(NullPointerException.class, () -> uniqueAssignmentList.contains((AssignmentName) null));
    }

    @Test
    public void contains_assignmentNotInList_returnsFalse() {
        assertFalse(uniqueAssignmentList.contains(PS1));
        assertFalse(uniqueAssignmentList.contains(NAME_PS1));
    }

    @Test
    public void contains_assignmentWithSameNameInDifferentCase_returnsTrue() {
        uniqueAssignmentList.add(PS1);
        assertTrue(uniqueAssignmentList.contains(new Assignment(new AssignmentName("ps1"))));
        assertTrue(uniqueAssignmentList.contains(new AssignmentName("ps1")));
    }

    @Test
    public void add_sameNameInDifferentCase_throwsDuplicateAssignmentException() {
        uniqueAssignmentList.add(PS1);
        Assignment sameName = new Assignment(new AssignmentName("ps1"));
        assertThrows(DuplicateAssignmentException.class, () -> uniqueAssignmentList.add(sameName));
    }

    @Test
    public void find_existingAndMissingName_correctResult() {
        uniqueAssignmentList.add(PS1);
        assertEquals(Optional.of(PS1), uniqueAssignmentList.find(new AssignmentName("ps1")));
        assertEquals(Optional.empty(), uniqueAssignmentList.find(PS2.getName()));
        assertThrows(NullPointerException.class, () -> uniqueAssignmentList.find(null));
    }

    @Test
    public void remove_assignmentDoesNotExist_throwsAssignmentNotFoundException() {
        assertThrows(AssignmentNotFoundException.class, () -> uniqueAssignmentList.remove(PS1));
    }

    @Test
    public void setAssignments_listWithDuplicateNames_throwsDuplicateAssignmentException() {
        List<Assignment> assignmentsWithSameName = List.of(PS1, new Assignment(new AssignmentName("ps1")));
        assertThrows(DuplicateAssignmentException.class, () ->
                uniqueAssignmentList.setAssignments(assignmentsWithSameName));
    }

    @Test
    public void setAssignments_validList_replacesContentsKeepingOrder() {
        uniqueAssignmentList.add(PS1);
        uniqueAssignmentList.setAssignments(List.of(PS2, PS1));
        assertEquals(List.of(PS2, PS1), uniqueAssignmentList.asUnmodifiableObservableList());
    }

    @Test
    public void setAssignment_existingName_replacesRecord() {
        uniqueAssignmentList.add(PS1);
        Assignment updated = PS1.withSubmissionStatus(new StudentId("A1234567A"), true);

        uniqueAssignmentList.setAssignment(updated);

        assertEquals(List.of(updated), uniqueAssignmentList.asUnmodifiableObservableList());
    }

    @Test
    public void setAssignment_missingName_throwsAssignmentNotFoundException() {
        assertThrows(AssignmentNotFoundException.class, () -> uniqueAssignmentList.setAssignment(PS1));
    }

    @Test
    public void equals() {
        uniqueAssignmentList.add(PS1);
        UniqueAssignmentList sameList = new UniqueAssignmentList();
        sameList.add(PS1);

        assertTrue(uniqueAssignmentList.equals(sameList));
        assertEquals(uniqueAssignmentList.hashCode(), sameList.hashCode());
        assertFalse(uniqueAssignmentList.equals(new UniqueAssignmentList()));
    }
}
