package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.awt.AWTError;
import java.awt.Taskbar;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
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
     * A missing or unreadable image, an unsupported platform, or a failure to set the icon is logged and otherwise
     * ignored, as the icon is not essential and must never stop TAssist from starting.
     *
     * @return true if the icon was set.
     */
    static boolean show(String imagePath) {
        requireNonNull(imagePath);
        Optional<BufferedImage> image = readImage(imagePath);
        if (image.isEmpty()) {
            return false;
        }
        if (!isIconImageSupported()) {
            logger.fine("The platform takes the task bar icon from the window.");
            return false;
        }

        try {
            Taskbar.getTaskbar().setIconImage(image.get());
            logger.fine("Set the task bar icon to " + imagePath);
            return true;
        } catch (RuntimeException e) {
            // macOS wraps a failure to set the Dock icon in a RuntimeException. The icon is not essential,
            // so log it and let the app start anyway.
            logger.warning("Could not set the task bar icon: " + e);
            return false;
        }
    }

    /**
     * Returns the image at {@code imagePath}, a resource path, or an empty {@code Optional} if it is missing or is
     * not an image. Either problem is logged.
     */
    static Optional<BufferedImage> readImage(String imagePath) {
        requireNonNull(imagePath);
        try (InputStream imageStream = MainApp.class.getResourceAsStream(imagePath)) {
            if (imageStream == null) {
                logger.warning("Task bar icon not found: " + imagePath);
                return Optional.empty();
            }
            Optional<BufferedImage> image = Optional.ofNullable(ImageIO.read(imageStream));
            if (image.isEmpty()) {
                logger.warning("Task bar icon is not a readable image: " + imagePath);
            }
            return image;
        } catch (IOException e) {
            logger.warning("Could not read the task bar icon " + imagePath + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Returns true if the platform lets Java set the task bar or Dock icon. A headless or missing desktop counts
     * as unsupported.
     */
    private static boolean isIconImageSupported() {
        try {
            return Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE);
        } catch (AWTError | RuntimeException e) {
            return false;
        }
    }
}
