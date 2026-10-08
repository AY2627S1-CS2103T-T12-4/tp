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
    public void describeEmptyState_activeGroup_namesGroup() {
        assertEquals("No students in T03 yet",
                StudentListPanel.describeEmptyTitle(Optional.of(TypicalGroups.getT03())));
        assertEquals("Students you add to T03 will appear here.",
                StudentListPanel.describeEmptyHint(Optional.of(TypicalGroups.getT03())));
    }

    @Test
    public void describeEmptyState_noActiveGroup_explainsHowToSeeStudents() {
        assertEquals(StudentListPanel.NO_ACTIVE_GROUP, StudentListPanel.describeEmptyTitle(Optional.empty()));
        assertEquals(StudentListPanel.NO_ACTIVE_GROUP_HINT, StudentListPanel.describeEmptyHint(Optional.empty()));
    }
}
