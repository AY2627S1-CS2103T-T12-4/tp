package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.awt.AWTError;
import java.awt.Taskbar;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import seedu.address.MainApp;
import seedu.address.commons.core.LogsCenter;

/**
 * Sets the icon that the operating system shows for TAssist in its task bar or Dock.
 * JavaFX window icons do not reach the macOS Dock, so this uses the Java platform's {@link Taskbar} where it is
 * supported. Other platforms, which take the icon from the window, are left unchanged.
 */
final class TaskbarIcon {
    private static final Logger logger = LogsCenter.getLogger(TaskbarIcon.class);

    private TaskbarIcon() {}

    /**
     * Shows the image at {@code imagePath}, a resource path, as the task bar or Dock icon if the platform supports it.
     * A missing image or an unsupported platform is logged and otherwise ignored, as the icon is not essential.
     *
     * @return true if the icon was set.
     */
    static boolean show(String imagePath) {
        requireNonNull(imagePath);
        if (!isIconImageSupported()) {
            logger.fine("The platform takes the task bar icon from the window.");
            return false;
        }

        try (InputStream imageStream = MainApp.class.getResourceAsStream(imagePath)) {
            if (imageStream == null) {
                logger.warning("Task bar icon not found: " + imagePath);
                return false;
            }
            BufferedImage image = ImageIO.read(imageStream);
            if (image == null) {
                logger.warning("Task bar icon is not a readable image: " + imagePath);
                return false;
            }
            Taskbar.getTaskbar().setIconImage(image);
            logger.fine("Set the task bar icon to " + imagePath);
            return true;
        } catch (IOException | UnsupportedOperationException | SecurityException e) {
            logger.warning("Could not set the task bar icon: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns true if the platform lets Java set the task bar or Dock icon. A headless or missing desktop counts
     * as unsupported.
     */
    private static boolean isIconImageSupported() {
        try {
            return Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE);
        } catch (AWTError | UnsupportedOperationException e) {
            return false;
        }
    }
}
