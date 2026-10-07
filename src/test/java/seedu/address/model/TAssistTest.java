package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T02;
import static seedu.address.testutil.TypicalGroups.NAME_T03;
import static seedu.address.testutil.TypicalGroups.NAME_T04;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;
import static seedu.address.testutil.TypicalStudents.CARL;
import static seedu.address.testutil.TypicalStudents.DANIEL;
import static seedu.address.testutil.TypicalStudents.ELLE;
import static seedu.address.testutil.TypicalStudents.FIONA;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.exceptions.DuplicateGroupException;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class TAssistTest {

    private final TAssist tAssist = new TAssist();
    private final TAssist typicalTAssist = TypicalGroups.getTypicalTAssist();

    @Test
    public void constructor_newTAssist_isEmptyWithNoActiveGroup() {
        assertTrue(tAssist.getGroupList().isEmpty());
        assertEquals(Optional.empty(), tAssist.getActiveGroup());
    }

    @Test
    public void addGroup_newGroups_groupsListedInOrder() {
        tAssist.addGroup(TypicalGroups.getT01());
        tAssist.addGroup(TypicalGroups.getT02());
        assertEquals(List.of(TypicalGroups.getT01(), TypicalGroups.getT02()), tAssist.getGroupList());
        assertTrue(tAssist.hasGroup(new GroupName("t02")));
    }

    @Test
    public void addGroup_duplicateNameInDifferentCase_throwsDuplicateGroupException() {
        Group sameName = new Group(new GroupName("t01"));
        assertThrows(DuplicateGroupException.class, () -> typicalTAssist.addGroup(sameName));
    }

    @Test
    public void addGroup_doesNotChangeActiveGroup() {
        typicalTAssist.addGroup(new Group(NAME_T04));
        assertEquals(Optional.empty(), typicalTAssist.getActiveGroup());
    }

    @Test
    public void getGroup_missingGroup_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> typicalTAssist.getGroup(NAME_T04));
    }

    @Test
    public void setActiveGroup_existingGroupInDifferentCase_setsActiveGroup() {
        typicalTAssist.setActiveGroup(new GroupName("t02"));
        assertEquals(Optional.of(TypicalGroups.getT02()), typicalTAssist.getActiveGroup());
    }

    @Test
    public void setActiveGroup_missingGroup_throwsGroupNotFoundExceptionAndKeepsActiveGroup() {
        TAssist tAssistWithActiveGroup = new TAssistBuilder(typicalTAssist).withActiveGroup(NAME_T01).build();
        assertThrows(GroupNotFoundException.class, () -> tAssistWithActiveGroup.setActiveGroup(NAME_T04));
        assertEquals(Optional.of(TypicalGroups.getT01()), tAssistWithActiveGroup.getActiveGroup());
    }

    @Test
    public void addStudent_existingGroup_studentAddedToThatGroupOnly() {
        typicalTAssist.addStudent(new GroupName("t03"), FIONA);
        assertEquals(List.of(FIONA), typicalTAssist.getGroup(NAME_T03).getStudentList());
        assertEquals(List.of(ALICE, BEN, CARL), typicalTAssist.getGroup(NAME_T01).getStudentList());
        assertEquals(List.of(DANIEL, ELLE), typicalTAssist.getGroup(NAME_T02).getStudentList());
    }

    @Test
    public void addStudent_sameStudentIdInDifferentGroups_success() {
        typicalTAssist.addStudent(NAME_T02, ALICE);
        assertTrue(typicalTAssist.getGroup(NAME_T01).hasStudent(ALICE.getStudentId()));
        assertTrue(typicalTAssist.getGroup(NAME_T02).hasStudent(ALICE.getStudentId()));
    }

    @Test
    public void addStudent_duplicateStudentIdInGroup_throwsDuplicateStudentException() {
        Student sameId = new StudentBuilder(ALICE).withName("Alicia Tan").withStudentId("a0123456x").build();
        assertThrows(DuplicateStudentException.class, () -> typicalTAssist.addStudent(NAME_T01, sameId));
    }

    @Test
    public void addStudent_missingGroup_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> typicalTAssist.addStudent(NAME_T04, FIONA));
    }

    @Test
    public void addStudent_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> typicalTAssist.addStudent(null, FIONA));
        assertThrows(NullPointerException.class, () -> typicalTAssist.addStudent(NAME_T01, null));
    }

    @Test
    public void removeStudent_existingStudent_removesAndReturnsStudent() {
        assertEquals(ALICE, typicalTAssist.removeStudent(NAME_T01, new StudentId("a0123456x")));
        assertEquals(List.of(BEN, CARL), typicalTAssist.getGroup(NAME_T01).getStudentList());
    }

    @Test
    public void removeStudent_missingStudentOrGroup_throwsException() {
        assertThrows(StudentNotFoundException.class, () ->
                typicalTAssist.removeStudent(NAME_T01, FIONA.getStudentId()));
        assertThrows(GroupNotFoundException.class, () ->
                typicalTAssist.removeStudent(NAME_T04, ALICE.getStudentId()));
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> tAssist.resetData(null));
    }

    @Test
    public void copyConstructor_changeCopy_originalUnchanged() {
        typicalTAssist.setActiveGroup(NAME_T01);

        TAssist copy = new TAssist(typicalTAssist);
        assertEquals(typicalTAssist, copy);

        copy.addStudent(NAME_T01, FIONA);
        copy.addGroup(new Group(NAME_T04));
        copy.setActiveGroup(NAME_T04);
        assertEquals(List.of(ALICE, BEN, CARL), typicalTAssist.getGroup(NAME_T01).getStudentList());
        assertFalse(typicalTAssist.hasGroup(NAME_T04));
        assertEquals(NAME_T01, typicalTAssist.getActiveGroup().get().getName());
    }

    @Test
    public void getGroupList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () ->
                typicalTAssist.getGroupList().add(new Group(NAME_T04)));
    }

    @Test
    public void equals() {
        TAssist sameData = new TAssist(typicalTAssist);

        // same values -> returns true
        assertTrue(typicalTAssist.equals(sameData));
        assertEquals(typicalTAssist.hashCode(), sameData.hashCode());

        // same object -> returns true
        assertTrue(typicalTAssist.equals(typicalTAssist));

        // null -> returns false
        assertFalse(typicalTAssist.equals(null));

        // different type -> returns false
        assertFalse(typicalTAssist.equals(5));

        // different active group -> returns false
        sameData.setActiveGroup(NAME_T01);
        assertFalse(typicalTAssist.equals(sameData));

        // different groups -> returns false
        assertFalse(typicalTAssist.equals(new TAssist()));
    }

    @Test
    public void toStringMethod() {
        String expected = TAssist.class.getCanonicalName() + "{groups=" + tAssist.getGroupList()
                + ", activeGroup=null}";
        assertEquals(expected, tAssist.toString());
    }
}
