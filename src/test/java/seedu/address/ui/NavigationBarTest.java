package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.WorkspaceView;

public class NavigationBarTest {

    @Test
    public void describe_availableScreen_namesEquivalentCommand() {
        assertEquals("Same as typing: view students", NavigationBar.describe(WorkspaceView.STUDENTS));
        assertEquals("Same as typing: view help", NavigationBar.describe(WorkspaceView.HELP));
    }

    @Test
    public void describe_plannedScreen_saysComingSoon() {
        assertEquals("Coming soon. Same as typing: view attendance", NavigationBar.describe(WorkspaceView.ATTENDANCE));
    }
}
