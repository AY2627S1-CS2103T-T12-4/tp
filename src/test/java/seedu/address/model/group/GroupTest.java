package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;

public class GroupTest {

    private static final GroupName NAME_T01 = new GroupName("T01");
    private static final Student ALICE = new Student(new StudentName("Alice Tan"), new StudentId("A0123456X"));
    private static final Student BEN = new Student(new StudentName("Ben Lim"), new StudentId("A0234567Y"));

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
    public void addStudent_newStudent_studentInGroup() {
        group.addStudent(ALICE);
        assertTrue(group.hasStudent(new StudentId("a0123456x")));
        assertEquals(Optional.of(ALICE), group.findStudent(ALICE.getStudentId()));
        assertFalse(group.hasStudent(BEN.getStudentId()));
    }

    @Test
    public void addStudent_duplicateStudentId_throwsDuplicateStudentException() {
        group.addStudent(ALICE);
        Student sameId = new Student(new StudentName("Alicia Tan"), ALICE.getStudentId());
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
    public void removeStudent_missingStudent_throwsStudentNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> group.removeStudent(ALICE.getStudentId()));
    }

    @Test
    public void getStudentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> group.getStudentList().add(ALICE));
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

        // different name -> returns false
        Group differentName = new Group(new GroupName("T02"));
        differentName.addStudent(ALICE);
        assertFalse(group.equals(differentName));
    }

    @Test
    public void toStringMethod() {
        String expected = Group.class.getCanonicalName() + "{name=" + NAME_T01 + ", students=[]}";
        assertEquals(expected, group.toString());
    }
}
