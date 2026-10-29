package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.TypicalGroups;

public class StudentListPanelTest {

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
                StudentListPanel.describeEmptyHint(Optional.of(TypicalGroups.getT03()), true, false, false));
    }

    @Test
    public void describeEmptyState_noActiveGroup_explainsHowToSeeStudents() {
        assertEquals(StudentListPanel.NO_ACTIVE_GROUP,
                StudentListPanel.describeEmptyTitle(Optional.empty(), false, false));
        assertEquals(StudentListPanel.NO_ACTIVE_GROUP_HINT,
                StudentListPanel.describeEmptyHint(Optional.empty(), false, false, false));
    }

    @Test
    public void describeEmptyState_groupsExistButNoneActive_suggestsGroupUse() {
        assertEquals(StudentListPanel.NO_ACTIVE_GROUP,
                StudentListPanel.describeEmptyTitle(Optional.empty(), false, false));
        assertEquals(StudentListPanel.SELECT_GROUP_HINT,
                StudentListPanel.describeEmptyHint(Optional.empty(), true, false, false));
    }

    @Test
    public void describeEmptyState_dataLoadFailed_saysSavedDataNotRead() {
        assertEquals(StudentListPanel.DATA_NOT_READ,
                StudentListPanel.describeEmptyTitle(Optional.empty(), false, true));
        assertEquals(StudentListPanel.DATA_NOT_READ_HINT,
                StudentListPanel.describeEmptyHint(Optional.empty(), false, false, true));
        assertEquals(StudentListPanel.DATA_NOT_READ_HINT,
                StudentListPanel.describeEmptyHint(Optional.empty(), true, false, true));
    }

    @Test
    public void describeEmptyState_populatedGroup_explainsHowToClearSearch() {
        assertEquals("No matching students",
                StudentListPanel.describeEmptyTitle(Optional.of(TypicalGroups.getT01()), true, false));
        assertEquals("Use student list to show all students.",
                StudentListPanel.describeEmptyHint(Optional.of(TypicalGroups.getT01()), true, true, false));
    }

    @Test
    public void describeEmptyState_emptyFilteredGroup_explainsHowToClearSearch() {
        assertEquals("No matching students",
                StudentListPanel.describeEmptyTitle(Optional.of(TypicalGroups.getT03()), true, false));
        assertEquals("Use student list to show all students.",
                StudentListPanel.describeEmptyHint(Optional.of(TypicalGroups.getT03()), true, true, false));
    }
}
