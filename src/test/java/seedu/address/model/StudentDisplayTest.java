package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T02;
import static seedu.address.testutil.TypicalGroups.NAME_T03;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;
import static seedu.address.testutil.TypicalStudents.CARL;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentSearchPredicate;
import seedu.address.model.student.StudentSortField;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TypicalGroups;

public class StudentDisplayTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T01);
        model.removeStudent(NAME_T01, ALICE.getStudentId());
        model.addStudent(NAME_T01, ALICE);
    }

    @Test
    public void getDisplayedStudentList_rosterChanges_keepsLiveFilteredAndSortedView() {
        ObservableList<Student> displayed = model.getDisplayedStudentList();
        model.filterStudents(new StudentSearchPredicate("a", ""));
        model.sortStudents(StudentSortField.NAME.getComparator());
        assertEquals(List.of(ALICE, CARL), displayed);

        Student aaron = new StudentBuilder().withName("Aaron").withStudentId("A1").build();
        model.addStudent(NAME_T01, aaron);
        assertEquals(List.of(aaron, ALICE, CARL), displayed);
        model.removeStudent(NAME_T01, CARL.getStudentId());
        Student updatedAlice = ALICE.withParticipationRecord(1, 5);
        model.setStudent(NAME_T01, updatedAlice);
        assertEquals(List.of(aaron, updatedAlice), displayed);

        model.filterStudents(new StudentSearchPredicate("Ben", ""));
        assertEquals(List.of(BEN), displayed);
        model.resetStudentDisplay();
        assertEquals(List.of(BEN, updatedAlice, aaron), displayed);
    }

    @Test
    public void renameGroup_activeOrInactiveGroup_preservesSearchSortingAndLiveRoster() {
        model.filterStudents(new StudentSearchPredicate("a", ""));
        model.sortStudents(StudentSortField.NAME.getComparator());
        StudentDisplayState previousDisplay = model.getStudentDisplayState();
        GroupName renamed = new GroupName("T01-Thu");
        model.renameGroup(NAME_T01, renamed);
        assertEquals(previousDisplay, model.getStudentDisplayState());
        assertEquals(List.of(ALICE, CARL), model.getDisplayedStudentList());
        model.removeStudent(renamed, ALICE.getStudentId());
        assertEquals(List.of(CARL), model.getDisplayedStudentList());
        model.renameGroup(NAME_T02, new GroupName("T02-Fri"));
        assertEquals(previousDisplay, model.getStudentDisplayState());
        assertEquals(renamed, model.getActiveGroup().orElseThrow().getName());
        model.setActiveGroup(new GroupName("T02-Fri"));
        assertFalse(model.studentFilterActiveProperty().get());
    }

    @Test
    public void setActiveGroup_differentGroup_resetsSearchAndSorting() {
        model.filterStudents(new StudentSearchPredicate("Alice", ""));
        model.sortStudents(StudentSortField.ID.getComparator());
        model.setActiveGroup(NAME_T02);
        assertEquals(model.getGroup(NAME_T02).getStudentList(), model.getDisplayedStudentList());
        model.setActiveGroup(NAME_T01);
        assertEquals(List.of(BEN, CARL, ALICE), model.getDisplayedStudentList());
    }

    @Test
    public void setTAssist_sameGroup_keepsSearchAndSortingAndFollowsReplacementRoster() {
        model.filterStudents(new StudentSearchPredicate("a", ""));
        model.sortStudents(StudentSortField.NAME.getComparator());
        model.setActiveGroup(NAME_T01);
        model.setTAssist(new TAssist(model.getTAssist()));
        assertEquals(List.of(ALICE, CARL), model.getDisplayedStudentList());
        model.removeStudent(NAME_T01, ALICE.getStudentId());
        assertEquals(List.of(CARL), model.getDisplayedStudentList());
        model.setTAssist(new TAssist());
        assertTrue(model.getDisplayedStudentList().isEmpty());
    }

    @Test
    public void getDisplayedStudentList_mutation_rejectsExternalChanges() {
        assertThrows(UnsupportedOperationException.class, () -> model.getDisplayedStudentList().clear());
        assertThrows(NullPointerException.class, () -> model.filterStudents(null));
        assertThrows(NullPointerException.class, () -> model.sortStudents(null));
        assertThrows(NullPointerException.class, () -> model.setStudentDisplayState(null));
        assertFalse(model.getActiveGroupStudentList().isEmpty());
    }

    @Test
    public void filterStudents_inactiveGroupAndNonmatchingStudent_keepsOnlyActiveMatches() {
        model.filterStudents(new StudentSearchPredicate("Alice", ""));
        model.addStudent(NAME_T02, ALICE);
        Student zoe = new StudentBuilder().withName("Zoe").withStudentId("Z9").build();
        model.addStudent(NAME_T01, zoe);
        assertEquals(List.of(ALICE), model.getDisplayedStudentList());
        assertEquals(4, model.getActiveGroupStudentList().size());
    }

    @Test
    public void setStudentDisplayState_afterReset_restoresSearchAndSortOrder() {
        model.filterStudents(new StudentSearchPredicate("a", ""));
        model.sortStudents(StudentSortField.NAME.getComparator());
        StudentDisplayState previousDisplay = model.getStudentDisplayState();
        assertTrue(model.studentFilterActiveProperty().get());
        model.resetStudentDisplay();
        assertFalse(model.studentFilterActiveProperty().get());
        assertEquals(model.getActiveGroupStudentList(), model.getDisplayedStudentList());
        model.setStudentDisplayState(previousDisplay);
        assertTrue(model.studentFilterActiveProperty().get());
        assertEquals(List.of(ALICE, CARL), model.getDisplayedStudentList());
    }

    @Test
    public void studentFilterActiveProperty_emptyRoster_followsDisplaySettings() {
        model.setActiveGroup(NAME_T03);
        assertFalse(model.studentFilterActiveProperty().get());
        model.filterStudents(new StudentSearchPredicate("missing", ""));
        StudentDisplayState previousDisplay = model.getStudentDisplayState();
        assertTrue(model.studentFilterActiveProperty().get());
        model.resetStudentDisplay();
        assertFalse(model.studentFilterActiveProperty().get());
        model.setStudentDisplayState(previousDisplay);
        assertTrue(model.studentFilterActiveProperty().get());
        model.setActiveGroup(NAME_T02);
        assertFalse(model.studentFilterActiveProperty().get());
    }
}
