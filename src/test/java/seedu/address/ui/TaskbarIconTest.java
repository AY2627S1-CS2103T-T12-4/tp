package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

public class TaskbarIconTest {
    private static final String APP_ICON = "/images/tassist_256.png";
    private static final String MISSING_IMAGE = "/images/missing.png";
    private static final String NOT_AN_IMAGE = "/view/TAssist.css";

    @Test
    public void readImage_appIcon_returnsFullSizeImage() {
        BufferedImage image = TaskbarIcon.readImage(APP_ICON).orElseThrow();
        assertEquals(256, image.getWidth());
        assertEquals(256, image.getHeight());
    }

    @Test
    public void readImage_missingImage_returnsEmpty() {
        assertTrue(TaskbarIcon.readImage(MISSING_IMAGE).isEmpty());
    }

    @Test
    public void readImage_notAnImage_returnsEmpty() {
        assertTrue(TaskbarIcon.readImage(NOT_AN_IMAGE).isEmpty());
    }

    @Test
    public void show_appIcon_doesNotThrow() {
        assertDoesNotThrow(() -> TaskbarIcon.show(APP_ICON));
    }

    @Test
    public void show_missingImage_returnsFalse() {
        assertFalse(TaskbarIcon.show(MISSING_IMAGE));
    }

    @Test
    public void show_notAnImage_returnsFalse() {
        assertFalse(TaskbarIcon.show(NOT_AN_IMAGE));
    }

    @Test
    public void nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> TaskbarIcon.show(null));
        assertThrows(NullPointerException.class, () -> TaskbarIcon.readImage(null));
    }
}
