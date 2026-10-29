package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TypicalGroups;
import seedu.address.testutil.TypicalStudents;

public class StudentListPanelTest {

    @Test
    public void followStudentAdditions_modelReplacement_scrollsToSortedDisplayedRow() {
        ModelManager model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
        model.sortStudents(Comparator.comparing(student -> student.getName().toString()));
        List<Integer> rows = new ArrayList<>();
        List<Runnable> pending = new ArrayList<>();
        StudentListPanel.followStudentAdditions(model.activeGroupProperty(), model.getActiveGroupStudentList(),
                model.getDisplayedStudentList(), rows::add, pending::add);
        var added = new StudentBuilder().withName("Aaron").withStudentId("NEW1").build();
        model.addStudent(TypicalGroups.NAME_T01, added);
        pending.forEach(Runnable::run);
        assertEquals(List.of(model.getDisplayedStudentList().indexOf(added)), rows);
        assertEquals(List.of(0), rows);
    }

    @Test
    public void followStudentAdditions_switchSearchSortEditAndRemove_doesNotScroll() {
        ModelManager model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        GroupName singleGroup = new GroupName("Single");
        model.addGroup(new Group(singleGroup));
        model.addStudent(singleGroup, TypicalStudents.ALICE);
        model.setActiveGroup(TypicalGroups.NAME_T03);
        List<Integer> rows = new ArrayList<>();
        List<Runnable> pending = new ArrayList<>();
        StudentListPanel.followStudentAdditions(model.activeGroupProperty(), model.getActiveGroupStudentList(),
                model.getDisplayedStudentList(), rows::add, pending::add);
        model.setActiveGroup(singleGroup); // Empty to one student must not scroll.
        pending.forEach(Runnable::run);
        pending.clear();
        model.setActiveGroup(TypicalGroups.NAME_T01);
        model.filterStudents(student -> false);
        model.resetStudentDisplay();
        model.sortStudents(Comparator.comparing((Student student) -> student.getName().toString()).reversed());
        model.setStudent(TypicalGroups.NAME_T01,
                new StudentBuilder(TypicalStudents.ALICE).withName("Updated Alice").build());
        model.removeStudent(TypicalGroups.NAME_T01, TypicalStudents.BEN.getStudentId());
        pending.forEach(Runnable::run);
        assertEquals(List.of(), rows);
    }

    @Test
    public void followStudentAdditions_filteredOrRemovedBeforeDeferredScroll_doesNotScroll() {
        ModelManager model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T03);
        List<Integer> rows = new ArrayList<>();
        List<Runnable> pending = new ArrayList<>();
        StudentListPanel.followStudentAdditions(model.activeGroupProperty(), model.getActiveGroupStudentList(),
                model.getDisplayedStudentList(), rows::add, pending::add);
        model.filterStudents(student -> false);
        model.addStudent(TypicalGroups.NAME_T03, TypicalStudents.ALICE);
        pending.forEach(Runnable::run);
        pending.clear();
        model.resetStudentDisplay();
        model.addStudent(TypicalGroups.NAME_T03, TypicalStudents.BEN);
        model.removeStudent(TypicalGroups.NAME_T03, TypicalStudents.BEN.getStudentId());
        pending.forEach(Runnable::run);
        assertEquals(List.of(), rows);
    }

    @Test
    public void describeCount_oneStudent_usesSingular() {
        assertEquals("1 student", StudentListPanel.describeCount(1));
    }

    @Test
    public void describeCount_otherCounts_usesPlural() {
        assertEquals("0 students", StudentListPanel.describeCount(0));
        assertEquals("20 students", StudentListPanel.describeCount(20));
    }

    @Test
    public void describeTitle_activeGroup_namesGroup() {
        assertEquals("T03", StudentListPanel.describeTitle(Optional.of(TypicalGroups.getT03())));
    }

    @Test
    public void describeTitle_noActiveGroup_usesGeneralTitle() {
        assertEquals(StudentListPanel.STUDENTS_TITLE, StudentListPanel.describeTitle(Optional.empty()));
    }

    @Test
    public void describeEmptyState_activeGroup_namesGroup() {
        assertEquals("No students in T03 yet",
                StudentListPanel.describeEmptyTitle(Optional.of(TypicalGroups.getT03()), false, false));
        assertEquals("Students you add to T03 will appear here.",
                StudentListPanel.describeEmptyHint(Optional.of(TypicalGroups.getT03()), false, false));
    }

    @Test
    public void describeEmptyState_noActiveGroup_explainsHowToSeeStudents() {
        assertEquals(StudentListPanel.NO_ACTIVE_GROUP,
                StudentListPanel.describeEmptyTitle(Optional.empty(), false, false));
        assertEquals(StudentListPanel.NO_ACTIVE_GROUP_HINT,
                StudentListPanel.describeEmptyHint(Optional.empty(), false, false));
    }

    @Test
    public void describeEmptyState_dataLoadFailed_saysSavedDataNotRead() {
        assertEquals(StudentListPanel.DATA_NOT_READ,
                StudentListPanel.describeEmptyTitle(Optional.empty(), false, true));
        assertEquals(StudentListPanel.DATA_NOT_READ_HINT,
                StudentListPanel.describeEmptyHint(Optional.empty(), false, true));
    }

    @Test
    public void describeEmptyState_populatedGroup_explainsHowToClearSearch() {
        assertEquals("No matching students",
                StudentListPanel.describeEmptyTitle(Optional.of(TypicalGroups.getT01()), true, false));
        assertEquals("Use student list to show all students.",
                StudentListPanel.describeEmptyHint(Optional.of(TypicalGroups.getT01()), true, false));
    }

    @Test
    public void describeEmptyState_emptyFilteredGroup_explainsHowToClearSearch() {
        assertEquals("No matching students",
                StudentListPanel.describeEmptyTitle(Optional.of(TypicalGroups.getT03()), true, false));
        assertEquals("Use student list to show all students.",
                StudentListPanel.describeEmptyHint(Optional.of(TypicalGroups.getT03()), true, false));
    }
}
