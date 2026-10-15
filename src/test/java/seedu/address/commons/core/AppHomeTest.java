package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class AppHomeTest {

    @Test
    public void homeDirectoryOf_jarFile_returnsParentFolder() {
        Path jarFile = Path.of("dist", "tassist.jar");
        assertEquals(absolute("dist"), AppHome.homeDirectoryOf(jarFile));
    }

    @Test
    public void homeDirectoryOf_upperCaseJarExtension_returnsParentFolder() {
        Path jarFile = Path.of("dist", "tassist.JAR");
        assertEquals(absolute("dist"), AppHome.homeDirectoryOf(jarFile));
    }

    @Test
    public void homeDirectoryOf_classesDirectory_returnsWorkingDirectory() {
        Path classesDirectory = Path.of("build", "classes", "java", "main");
        assertEquals(workingDirectory(), AppHome.homeDirectoryOf(classesDirectory));
    }

    @Test
    public void homeDirectoryOf_nullCodeSource_returnsWorkingDirectory() {
        assertEquals(workingDirectory(), AppHome.homeDirectoryOf(null));
    }

    @Test
    public void getHomeDirectory_notRunningFromJar_returnsWorkingDirectory() {
        Path homeDirectory = AppHome.getHomeDirectory();
        assertEquals(workingDirectory(), homeDirectory);
        assertTrue(homeDirectory.isAbsolute());
    }

    private Path absolute(String first) {
        return Path.of(first).toAbsolutePath().normalize();
    }

    private Path workingDirectory() {
        return Path.of(System.getProperty("user.dir")).toAbsolutePath().normalize();
    }
}
