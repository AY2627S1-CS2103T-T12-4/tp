package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.WorkspaceView;

public class NavigationBarTest {

    @Test
    public void describe_everyScreen_namesEquivalentCommand() {
        assertEquals("Same as typing: view students", NavigationBar.describe(WorkspaceView.STUDENTS));
        assertEquals("Same as typing: view help", NavigationBar.describe(WorkspaceView.HELP));
        assertEquals("Same as typing: view storage", NavigationBar.describe(WorkspaceView.STORAGE));
    }
}
