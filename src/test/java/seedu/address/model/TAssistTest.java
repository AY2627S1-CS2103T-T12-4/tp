package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.exceptions.DuplicateGroupException;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;

public class TAssistTest {

    private static final GroupName T01 = new GroupName("T01");
    private static final GroupName T02 = new GroupName("T02");
    private static final Student ALICE = new Student(new StudentName("Alice Tan"), new StudentId("A0123456X"));
    private static final Student BEN = new Student(new StudentName("Ben Lim"), new StudentId("A0234567Y"));

    private final TAssist tAssist = new TAssist();

    @Test
    public void constructor_newTAssist_isEmptyWithNoActiveGroup() {
        assertTrue(tAssist.getGroupList().isEmpty());
        assertEquals(Optional.empty(), tAssist.getActiveGroup());
    }

    @Test
    public void addGroup_newGroups_groupsListedInOrder() {
        tAssist.addGroup(new Group(T01));
        tAssist.addGroup(new Group(T02));
        assertEquals(List.of(new Group(T01), new Group(T02)), tAssist.getGroupList());
        assertTrue(tAssist.hasGroup(new GroupName("t02")));
    }

    @Test
    public void addGroup_duplicateNameInDifferentCase_throwsDuplicateGroupException() {
        tAssist.addGroup(new Group(T01));
        Group sameName = new Group(new GroupName("t01"));
        assertThrows(DuplicateGroupException.class, () -> tAssist.addGroup(sameName));
    }

    @Test
    public void addGroup_doesNotChangeActiveGroup() {
        tAssist.addGroup(new Group(T01));
        assertEquals(Optional.empty(), tAssist.getActiveGroup());
    }

    @Test
    public void getGroup_missingGroup_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> tAssist.getGroup(T01));
    }

    @Test
    public void setActiveGroup_existingGroupInDifferentCase_setsActiveGroup() {
        tAssist.addGroup(new Group(T01));
        tAssist.addGroup(new Group(T02));
        tAssist.setActiveGroup(new GroupName("t02"));
        assertEquals(Optional.of(new Group(T02)), tAssist.getActiveGroup());
    }

    @Test
    public void setActiveGroup_missingGroup_throwsGroupNotFoundExceptionAndKeepsActiveGroup() {
        tAssist.addGroup(new Group(T01));
        tAssist.setActiveGroup(T01);
        assertThrows(GroupNotFoundException.class, () -> tAssist.setActiveGroup(T02));
        assertEquals(T01, tAssist.getActiveGroup().get().getName());
    }

    @Test
    public void addStudent_existingGroup_studentAddedToThatGroupOnly() {
        tAssist.addGroup(new Group(T01));
        tAssist.addGroup(new Group(T02));
        tAssist.addStudent(new GroupName("t01"), ALICE);
        assertEquals(List.of(ALICE), tAssist.getGroup(T01).getStudentList());
        assertTrue(tAssist.getGroup(T02).getStudentList().isEmpty());
    }

    @Test
    public void addStudent_sameStudentIdInDifferentGroups_success() {
        tAssist.addGroup(new Group(T01));
        tAssist.addGroup(new Group(T02));
        tAssist.addStudent(T01, ALICE);
        tAssist.addStudent(T02, ALICE);
        assertTrue(tAssist.getGroup(T01).hasStudent(ALICE.getStudentId()));
        assertTrue(tAssist.getGroup(T02).hasStudent(ALICE.getStudentId()));
    }

    @Test
    public void addStudent_duplicateStudentIdInGroup_throwsDuplicateStudentException() {
        tAssist.addGroup(new Group(T01));
        tAssist.addStudent(T01, ALICE);
        Student sameId = new Student(new StudentName("Alicia Tan"), new StudentId("a0123456x"));
        assertThrows(DuplicateStudentException.class, () -> tAssist.addStudent(T01, sameId));
    }

    @Test
    public void addStudent_missingGroup_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> tAssist.addStudent(T01, ALICE));
    }

    @Test
    public void addStudent_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> tAssist.addStudent(null, ALICE));
        assertThrows(NullPointerException.class, () -> tAssist.addStudent(T01, null));
    }

    @Test
    public void removeStudent_existingStudent_removesAndReturnsStudent() {
        tAssist.addGroup(new Group(T01));
        tAssist.addStudent(T01, ALICE);
        tAssist.addStudent(T01, BEN);
        assertEquals(ALICE, tAssist.removeStudent(T01, new StudentId("a0123456x")));
        assertEquals(List.of(BEN), tAssist.getGroup(T01).getStudentList());
    }

    @Test
    public void removeStudent_missingStudentOrGroup_throwsException() {
        tAssist.addGroup(new Group(T01));
        assertThrows(StudentNotFoundException.class, () -> tAssist.removeStudent(T01, ALICE.getStudentId()));
        assertThrows(GroupNotFoundException.class, () -> tAssist.removeStudent(T02, ALICE.getStudentId()));
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> tAssist.resetData(null));
    }

    @Test
    public void copyConstructor_changeCopy_originalUnchanged() {
        tAssist.addGroup(new Group(T01));
        tAssist.addStudent(T01, ALICE);
        tAssist.setActiveGroup(T01);

        TAssist copy = new TAssist(tAssist);
        assertEquals(tAssist, copy);

        copy.addStudent(T01, BEN);
        copy.addGroup(new Group(T02));
        copy.setActiveGroup(T02);
        assertEquals(List.of(ALICE), tAssist.getGroup(T01).getStudentList());
        assertFalse(tAssist.hasGroup(T02));
        assertEquals(T01, tAssist.getActiveGroup().get().getName());
    }

    @Test
    public void getGroupList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> tAssist.getGroupList().add(new Group(T01)));
    }

    @Test
    public void equals() {
        tAssist.addGroup(new Group(T01));
        TAssist sameData = new TAssist(tAssist);

        // same values -> returns true
        assertTrue(tAssist.equals(sameData));
        assertEquals(tAssist.hashCode(), sameData.hashCode());

        // same object -> returns true
        assertTrue(tAssist.equals(tAssist));

        // null -> returns false
        assertFalse(tAssist.equals(null));

        // different type -> returns false
        assertFalse(tAssist.equals(5));

        // different active group -> returns false
        sameData.setActiveGroup(T01);
        assertFalse(tAssist.equals(sameData));

        // different groups -> returns false
        assertFalse(tAssist.equals(new TAssist()));
    }

    @Test
    public void toStringMethod() {
        String expected = TAssist.class.getCanonicalName() + "{groups=" + tAssist.getGroupList()
                + ", activeGroup=null}";
        assertEquals(expected, tAssist.toString());
    }
}
