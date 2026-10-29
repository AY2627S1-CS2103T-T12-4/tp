package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;
import static seedu.address.testutil.TypicalAssignments.PS1;
import static seedu.address.testutil.TypicalAssignments.PS2;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.exceptions.DuplicateAssignmentException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TypicalAssignments;

public class GroupTest {

    private final Group group = new Group(NAME_T01);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Group((GroupName) null));
        assertThrows(NullPointerException.class, () -> new Group((Group) null));
    }

    @Test
    public void constructor_newGroup_hasNoStudents() {
        assertEquals(NAME_T01, group.getName());
        assertTrue(group.getStudentList().isEmpty());
        assertTrue(group.getAssignmentList().isEmpty());
    }

    @Test
    public void copyConstructor_changeCopy_originalUnchanged() {
        group.addStudent(ALICE);
        Group copy = new Group(group);
        assertEquals(group, copy);

        copy.addStudent(BEN);
        assertEquals(List.of(ALICE), group.getStudentList());
        assertEquals(List.of(ALICE, BEN), copy.getStudentList());
    }

    @Test
    public void copyConstructor_withAssignments_copiesAssignmentsIndependently() {
        group.addAssignment(PS1);
        Group copy = new Group(group);
        assertEquals(group, copy);

        copy.addAssignment(PS2);
        assertEquals(List.of(PS1), group.getAssignmentList());
        assertEquals(List.of(PS1, PS2), copy.getAssignmentList());
    }

    @Test
    public void addStudent_newStudent_studentInGroup() {
        group.addStudent(ALICE);
        assertTrue(group.hasStudent(new StudentId("a0123456x")));
        assertEquals(Optional.of(ALICE), group.findStudent(ALICE.getStudentId()));
        assertFalse(group.hasStudent(BEN.getStudentId()));
    }

    @Test
    public void hasStudentNamed_ignoresCaseAndExtraSpaces() {
        group.addStudent(ALICE);
        assertTrue(group.hasStudentNamed(new StudentName("alice  TAN")));
        assertFalse(group.hasStudentNamed(new StudentName("Alice")));
        assertFalse(group.hasStudentNamed(BEN.getName()));
    }

    @Test
    public void addStudent_duplicateStudentId_throwsDuplicateStudentException() {
        group.addStudent(ALICE);
        Student sameId = new StudentBuilder(ALICE).withName("Alicia Tan").build();
        assertThrows(DuplicateStudentException.class, () -> group.addStudent(sameId));
    }

    @Test
    public void removeStudent_existingStudent_removesAndReturnsStudent() {
        group.addStudent(ALICE);
        group.addStudent(BEN);
        assertEquals(ALICE, group.removeStudent(ALICE.getStudentId()));
        assertEquals(List.of(BEN), group.getStudentList());
    }

    @Test
    public void removeStudent_existingStudent_removesOnlyTheirAssignmentRecords() {
        group.addStudent(ALICE);
        group.addStudent(BEN);
        Assignment ps1 = PS1.withSubmissionStatus(ALICE.getStudentId(), true)
                .withSubmissionStatus(BEN.getStudentId(), true);
        Assignment ps2 = PS2.withSubmissionStatus(ALICE.getStudentId(), true);
        group.addAssignment(ps1);
        group.addAssignment(ps2);

        group.removeStudent(ALICE.getStudentId());

        assertEquals(Map.of(BEN.getStudentId(), ps1.getStudentRecord(BEN.getStudentId())),
                group.findAssignment(PS1.getName()).orElseThrow().getStudentRecords());
        assertTrue(group.findAssignment(PS2.getName()).orElseThrow().getStudentRecords().isEmpty());
    }

    @Test
    public void replaceStudent_changedNameAndId_keepsPositionAndRecords() {
        Student recordedAlice = ALICE.withAttendanceRecord(1, "PRESENT").withParticipationRecord(1, 4);
        group.addStudent(recordedAlice);
        group.addStudent(BEN);
        Student edited = recordedAlice.withNameAndId(new StudentName("Alicia Tan"), new StudentId("A0123457X"));

        group.replaceStudent(ALICE.getStudentId(), edited);

        assertEquals(List.of(edited, BEN), group.getStudentList());
        assertFalse(group.hasStudent(ALICE.getStudentId()));
    }

    @Test
    public void replaceStudent_changedId_movesOnlyTheirAssignmentRecords() {
        group.addStudent(ALICE);
        group.addStudent(BEN);
        StudentId newId = new StudentId("A0123457X");
        Assignment ps1 = PS1.withSubmissionStatus(ALICE.getStudentId(), true)
                .withGrade(ALICE.getStudentId(), 87).withSubmissionStatus(BEN.getStudentId(), true);
        group.addAssignment(ps1);
        group.addAssignment(PS2);

        group.replaceStudent(ALICE.getStudentId(), ALICE.withNameAndId(ALICE.getName(), newId));

        Assignment updatedPs1 = group.findAssignment(PS1.getName()).orElseThrow();
        assertEquals(ps1.getStudentRecord(ALICE.getStudentId()), updatedPs1.getStudentRecord(newId));
        assertEquals(ps1.getStudentRecord(BEN.getStudentId()), updatedPs1.getStudentRecord(BEN.getStudentId()));
        assertEquals(2, updatedPs1.getStudentRecords().size());
        assertFalse(updatedPs1.getStudentRecords().containsKey(ALICE.getStudentId()));
        assertEquals(List.of(updatedPs1, PS2), group.getAssignmentList());
    }

    @Test
    public void replaceStudent_idOfAnotherStudent_throwsDuplicateStudentExceptionAndKeepsGroup() {
        group.addStudent(ALICE);
        group.addStudent(BEN);
        group.addAssignment(PS1.withSubmissionStatus(ALICE.getStudentId(), true));
        Group before = new Group(group);

        Student clashing = ALICE.withNameAndId(ALICE.getName(), BEN.getStudentId());

        assertThrows(DuplicateStudentException.class, () -> group.replaceStudent(ALICE.getStudentId(), clashing));
        assertEquals(before, group);
    }

    @Test
    public void replaceStudent_missingStudentOrNullReplacement_throwsException() {
        assertThrows(StudentNotFoundException.class, () -> group.replaceStudent(ALICE.getStudentId(), BEN));
        assertThrows(NullPointerException.class, () -> group.replaceStudent(ALICE.getStudentId(), null));
    }

    @Test
    public void removeStudent_missingStudent_throwsStudentNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> group.removeStudent(ALICE.getStudentId()));
    }

    @Test
    public void setStudent_existingId_replacesStudentInPlace() {
        group.addStudent(ALICE);
        group.addStudent(BEN);
        Student updatedAlice = ALICE.withAttendanceRecord(1, "PRESENT");

        group.setStudent(updatedAlice);

        assertEquals(List.of(updatedAlice, BEN), group.getStudentList());
    }

    @Test
    public void setStudent_laterStudent_replacesStudentInPlace() {
        group.addStudent(ALICE);
        group.addStudent(BEN);
        Student updatedBen = BEN.withParticipationRecord(2, 3);

        group.setStudent(updatedBen);

        assertEquals(List.of(ALICE, updatedBen), group.getStudentList());
    }

    @Test
    public void setStudent_missingIdOrNull_throwsException() {
        assertThrows(StudentNotFoundException.class, () -> group.setStudent(ALICE));
        assertThrows(NullPointerException.class, () -> group.setStudent(null));
    }

    @Test
    public void getStudentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> group.getStudentList().add(ALICE));
    }

    @Test
    public void addAssignment_newAssignment_assignmentInGroup() {
        group.addAssignment(PS1);
        assertTrue(group.hasAssignment(new AssignmentName("ps1")));
        assertEquals(Optional.of(PS1), group.findAssignment(NAME_PS1));
        assertFalse(group.hasAssignment(PS2.getName()));
        assertEquals(Optional.empty(), group.findAssignment(PS2.getName()));
    }

    @Test
    public void addAssignment_assignmentsKeptInCreationOrder() {
        group.addAssignment(PS2);
        group.addAssignment(PS1);
        assertEquals(List.of(PS2, PS1), group.getAssignmentList());
    }

    @Test
    public void addAssignment_sameNameInDifferentCase_throwsDuplicateAssignmentException() {
        group.addAssignment(PS1);
        Assignment sameName = new Assignment(new AssignmentName("ps1"));
        assertThrows(DuplicateAssignmentException.class, () -> group.addAssignment(sameName));
    }

    @Test
    public void addAssignment_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> group.addAssignment(null));
    }

    @Test
    public void getAssignmentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> group.getAssignmentList().add(PS1));
    }

    @Test
    public void isSameGroup() {
        // same object -> returns true
        assertTrue(group.isSameGroup(group));

        // null -> returns false
        assertFalse(group.isSameGroup(null));

        // same name in a different case, different students -> returns true
        Group otherGroup = new Group(new GroupName("t01"));
        otherGroup.addStudent(ALICE);
        assertTrue(group.isSameGroup(otherGroup));

        // different name -> returns false
        assertFalse(group.isSameGroup(new Group(new GroupName("T02"))));
    }

    @Test
    public void equals() {
        group.addStudent(ALICE);
        Group sameGroup = new Group(NAME_T01);
        sameGroup.addStudent(ALICE);

        // same values -> returns true
        assertTrue(group.equals(sameGroup));
        assertEquals(group.hashCode(), sameGroup.hashCode());

        // same object -> returns true
        assertTrue(group.equals(group));

        // null -> returns false
        assertFalse(group.equals(null));

        // different type -> returns false
        assertFalse(group.equals(5));

        // different students -> returns false
        assertFalse(group.equals(new Group(NAME_T01)));

        // different assignments -> returns false
        Group differentAssignments = new Group(sameGroup);
        differentAssignments.addAssignment(PS1);
        assertFalse(group.equals(differentAssignments));

        // different name -> returns false
        Group differentName = new Group(new GroupName("T02"));
        differentName.addStudent(ALICE);
        assertFalse(group.equals(differentName));

        // same name in a different case -> returns false
        assertFalse(group.equals(sameGroup.withName(new GroupName("t01"))));
    }

    @Test
    public void withName_returnsCopyWithNewNameAndSameStudentsAndAssignments() {
        group.addStudent(ALICE);
        group.addAssignment(TypicalAssignments.PS1);

        Group renamed = group.withName(new GroupName("T09"));

        assertEquals(new GroupName("T09"), renamed.getName());
        assertEquals(group.getStudentList(), renamed.getStudentList());
        assertEquals(List.of(TypicalAssignments.PS1), renamed.getAssignmentList());
        assertEquals(NAME_T01, group.getName());
    }

    @Test
    public void toStringMethod() {
        String expected = Group.class.getCanonicalName() + "{name=" + NAME_T01 + ", students=[], assignments=[]}";
        assertEquals(expected, group.toString());
    }
}
