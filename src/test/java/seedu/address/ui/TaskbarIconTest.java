package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TaskbarIconTest {

    @Test
    public void show_appIcon_doesNotThrow() {
        assertDoesNotThrow(() -> TaskbarIcon.show("/images/tassist_256.png"));
    }

    @Test
    public void show_missingImage_returnsFalse() {
        assertFalse(TaskbarIcon.show("/images/missing.png"));
    }

    @Test
    public void show_notAnImage_returnsFalse() {
        assertFalse(TaskbarIcon.show("/view/TAssist.css"));
    }

    @Test
    public void show_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> TaskbarIcon.show(null));
    }
}
