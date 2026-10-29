package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.PS1;
import static seedu.address.testutil.TypicalAssignments.PS2;
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

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.exceptions.AssignmentNotFoundException;
import seedu.address.model.assignment.exceptions.DuplicateAssignmentException;
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
import seedu.address.testutil.TypicalAssignments;
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
    public void removeGroup_existingGroup_removesAndReturnsGroup() {
        typicalTAssist.setActiveGroup(TypicalGroups.NAME_T02);

        assertEquals(TypicalGroups.getT01(), typicalTAssist.removeGroup(new GroupName("t01")));

        assertEquals(List.of(TypicalGroups.getT02(), TypicalGroups.getT03()), typicalTAssist.getGroupList());
        assertEquals(Optional.of(TypicalGroups.getT02()), typicalTAssist.getActiveGroup());
    }

    @Test
    public void removeGroup_activeGroup_leavesNoActiveGroup() {
        typicalTAssist.setActiveGroup(TypicalGroups.NAME_T02);
        typicalTAssist.removeGroup(TypicalGroups.NAME_T02);
        assertEquals(Optional.empty(), typicalTAssist.getActiveGroup());
    }

    @Test
    public void removeGroup_missingGroup_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> typicalTAssist.removeGroup(TypicalGroups.NAME_T04));
    }

    @Test
    public void renameGroup_existingGroup_keepsStudentsAndPosition() {
        typicalTAssist.renameGroup(new GroupName("t02"), new GroupName("T02-Thu"));

        Group renamed = typicalTAssist.getGroupList().get(1);
        assertEquals("T02-Thu", renamed.getName().toString());
        assertEquals(TypicalGroups.getT02().getStudentList(), renamed.getStudentList());
        assertEquals(3, typicalTAssist.getGroupList().size());
    }

    @Test
    public void renameGroup_groupWithAssignments_keepsAssignments() {
        typicalTAssist.getGroup(TypicalGroups.NAME_T01).addAssignment(TypicalAssignments.PS1);

        typicalTAssist.renameGroup(TypicalGroups.NAME_T01, TypicalGroups.NAME_T04);

        assertEquals(List.of(TypicalAssignments.PS1),
                typicalTAssist.getGroup(TypicalGroups.NAME_T04).getAssignmentList());
    }

    @Test
    public void renameGroup_activeGroup_staysActiveUnderNewName() {
        typicalTAssist.setActiveGroup(TypicalGroups.NAME_T02);
        typicalTAssist.renameGroup(TypicalGroups.NAME_T02, TypicalGroups.NAME_T04);
        assertEquals(TypicalGroups.NAME_T04, typicalTAssist.getActiveGroup().orElseThrow().getName());
    }

    @Test
    public void renameGroup_onlyChangesCase_success() {
        typicalTAssist.renameGroup(TypicalGroups.NAME_T01, new GroupName("t01"));
        assertEquals("t01", typicalTAssist.getGroupList().get(0).getName().toString());
    }

    @Test
    public void renameGroup_nameOfAnotherGroup_throwsDuplicateGroupExceptionAndKeepsData() {
        assertThrows(DuplicateGroupException.class, () ->
                typicalTAssist.renameGroup(TypicalGroups.NAME_T01, new GroupName("t02")));
        assertEquals(TypicalGroups.getTypicalTAssist(), typicalTAssist);
    }

    @Test
    public void renameGroup_missingGroup_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () ->
                typicalTAssist.renameGroup(TypicalGroups.NAME_T04, new GroupName("T05")));
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
    public void addAssignment_existingGroup_assignmentAddedToThatGroupOnly() {
        typicalTAssist.addAssignment(new GroupName("t01"), PS1);
        assertEquals(List.of(PS1), typicalTAssist.getGroup(NAME_T01).getAssignmentList());
        assertTrue(typicalTAssist.getGroup(NAME_T02).getAssignmentList().isEmpty());
    }

    @Test
    public void addAssignment_sameNameInDifferentGroups_success() {
        typicalTAssist.addAssignment(NAME_T01, PS1);
        typicalTAssist.addAssignment(NAME_T02, PS1);
        assertTrue(typicalTAssist.getGroup(NAME_T01).hasAssignment(PS1.getName()));
        assertTrue(typicalTAssist.getGroup(NAME_T02).hasAssignment(PS1.getName()));
    }

    @Test
    public void addAssignment_duplicateNameInGroup_throwsDuplicateAssignmentException() {
        typicalTAssist.addAssignment(NAME_T01, PS1);
        Assignment sameName = new Assignment(new AssignmentName("ps1"));
        assertThrows(DuplicateAssignmentException.class, () -> typicalTAssist.addAssignment(NAME_T01, sameName));
    }

    @Test
    public void addAssignment_missingGroup_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> typicalTAssist.addAssignment(NAME_T04, PS2));
    }

    @Test
    public void addAssignment_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> typicalTAssist.addAssignment(null, PS1));
        assertThrows(NullPointerException.class, () -> typicalTAssist.addAssignment(NAME_T01, null));
    }

    @Test
    public void removeAssignment_existingAssignment_removesFromThatGroupOnly() {
        typicalTAssist.addAssignment(NAME_T01, PS1);
        typicalTAssist.addAssignment(NAME_T01, PS2);
        typicalTAssist.addAssignment(NAME_T02, PS1);

        assertEquals(PS1, typicalTAssist.removeAssignment(new GroupName("t01"), new AssignmentName("ps1")));
        assertEquals(List.of(PS2), typicalTAssist.getGroup(NAME_T01).getAssignmentList());
        assertEquals(List.of(PS1), typicalTAssist.getGroup(NAME_T02).getAssignmentList());
    }

    @Test
    public void removeAssignment_missingAssignmentOrGroup_throwsException() {
        assertThrows(AssignmentNotFoundException.class, () ->
                typicalTAssist.removeAssignment(NAME_T01, PS1.getName()));
        assertThrows(GroupNotFoundException.class, () -> typicalTAssist.removeAssignment(NAME_T04, PS1.getName()));
    }

    @Test
    public void removeAssignment_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> typicalTAssist.removeAssignment(null, PS1.getName()));
        assertThrows(NullPointerException.class, () -> typicalTAssist.removeAssignment(NAME_T01, null));
    }

    @Test
    public void renameAssignment_existingAssignment_renamesInThatGroupOnly() {
        typicalTAssist.addAssignment(NAME_T01, PS1);
        typicalTAssist.addAssignment(NAME_T02, PS1);

        typicalTAssist.renameAssignment(new GroupName("t01"), new AssignmentName("ps1"), PS2.getName());

        assertEquals(List.of(PS1.withName(PS2.getName())), typicalTAssist.getGroup(NAME_T01).getAssignmentList());
        assertEquals(List.of(PS1), typicalTAssist.getGroup(NAME_T02).getAssignmentList());
    }

    @Test
    public void renameAssignment_onlyChangesCase_dataIsNotEqualToOriginal() {
        typicalTAssist.addAssignment(NAME_T01, PS1);
        TAssist original = new TAssist(typicalTAssist);

        typicalTAssist.renameAssignment(NAME_T01, PS1.getName(), new AssignmentName("ps1"));

        assertFalse(original.equals(typicalTAssist));
    }

    @Test
    public void renameAssignment_missingAssignmentOrGroup_throwsException() {
        assertThrows(AssignmentNotFoundException.class, () ->
                typicalTAssist.renameAssignment(NAME_T01, PS1.getName(), PS2.getName()));
        assertThrows(GroupNotFoundException.class, () ->
                typicalTAssist.renameAssignment(NAME_T04, PS1.getName(), PS2.getName()));
    }

    @Test
    public void renameAssignment_nameOfAnotherAssignment_throwsDuplicateAssignmentException() {
        typicalTAssist.addAssignment(NAME_T01, PS1);
        typicalTAssist.addAssignment(NAME_T01, PS2);

        assertThrows(DuplicateAssignmentException.class, () ->
                typicalTAssist.renameAssignment(NAME_T01, PS1.getName(), new AssignmentName("ps2")));
    }

    @Test
    public void renameAssignment_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () ->
                typicalTAssist.renameAssignment(null, PS1.getName(), PS2.getName()));
        assertThrows(NullPointerException.class, () -> typicalTAssist.renameAssignment(NAME_T01, null, PS2.getName()));
        assertThrows(NullPointerException.class, () -> typicalTAssist.renameAssignment(NAME_T01, PS1.getName(), null));
    }

    @Test
    public void copyConstructor_withAssignments_originalUnchanged() {
        typicalTAssist.addAssignment(NAME_T01, PS1);
        TAssist copy = new TAssist(typicalTAssist);
        assertEquals(typicalTAssist, copy);

        copy.addAssignment(NAME_T01, PS2);
        assertEquals(List.of(PS1), typicalTAssist.getGroup(NAME_T01).getAssignmentList());
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
    public void resetData_activeGroupNotAmongGroups_throwsAssertionError() {
        ReadOnlyTAssist inconsistentData = new ReadOnlyTAssist() {
            @Override
            public ObservableList<Group> getGroupList() {
                return FXCollections.observableArrayList();
            }

            @Override
            public Optional<Group> getActiveGroup() {
                return Optional.of(TypicalGroups.getT01());
            }
        };

        assertThrows(AssertionError.class, () -> tAssist.resetData(inconsistentData));
    }

    @Test
    public void getActiveGroup_activeGroupNameNotAmongGroups_throwsAssertionError() throws Exception {
        Field activeGroupName = TAssist.class.getDeclaredField("activeGroupName");
        activeGroupName.setAccessible(true);
        activeGroupName.set(tAssist, NAME_T01);

        assertThrows(AssertionError.class, tAssist::getActiveGroup);
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
