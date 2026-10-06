package seedu.address.commons.core;

import java.util.Locale;

/**
 * Identifies the screens available in the TAssist workspace.
 */
public enum WorkspaceView {
    STUDENTS("Students"),
    GROUPS("Groups"),
    ATTENDANCE("Attendance"),
    PARTICIPATION("Participation"),
    ASSIGNMENTS("Assignments"),
    HELP("Help"),
    STORAGE("Storage");

    private final String title;

    WorkspaceView(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getKeyword() {
        return name().toLowerCase(Locale.ROOT);
    }
}
