package seedu.address.commons.core;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Locale;

/**
 * Locates the folder that contains the running JAR.
 * The preference, data, export, and log files are placed in this folder by default.
 */
public final class AppHome {

    private AppHome() {}

    /**
     * Returns the folder that contains the application JAR.
     * Returns the working directory when the application is not running from a JAR,
     * such as during tests or {@code ./gradlew run}.
     */
    public static Path getHomeDirectory() {
        Path homeDirectory = homeDirectoryOf(locateCodeSource());
        assert homeDirectory != null && homeDirectory.isAbsolute();
        return homeDirectory;
    }

    /**
     * Returns the home folder for {@code codeSource}.
     * A JAR file uses its parent folder. Any other location uses the working directory.
     */
    static Path homeDirectoryOf(Path codeSource) {
        if (codeSource != null && isJarFile(codeSource)) {
            Path parent = codeSource.toAbsolutePath().normalize().getParent();
            if (parent != null) {
                return parent;
            }
        }
        return workingDirectory();
    }

    /**
     * Returns the path of the code source that loaded this class, or {@code null} if it cannot be resolved.
     */
    private static Path locateCodeSource() {
        try {
            ProtectionDomain protectionDomain = AppHome.class.getProtectionDomain();
            if (protectionDomain == null) {
                return null;
            }

            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource == null || codeSource.getLocation() == null) {
                return null;
            }
            return Path.of(codeSource.getLocation().toURI());
        } catch (URISyntaxException | IllegalArgumentException | SecurityException exception) {
            return null;
        }
    }

    private static boolean isJarFile(Path codeSource) {
        Path fileName = codeSource.getFileName();
        return fileName != null && fileName.toString().toLowerCase(Locale.ROOT).endsWith(".jar");
    }

    private static Path workingDirectory() {
        return Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
    }
}
