package seedu.address;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.StudentId;

/**
 * Checks the help of the running app: that it covers every command, and that its examples work when typed.
 */
public class HelpIntegrationTest {
    /** Every command format, in the order the Help screen shows them. */
    private static final List<String> EXPECTED_FORMATS = List.of(
            "view SCREEN", "help [TOPIC]", "exit",
            "group add g/GROUP_NAME", "group list", "group use g/GROUP_NAME",
            "student add n/NAME id/STUDENT_ID", "student list", "student delete id/STUDENT_ID",
            "attendance mark id/STUDENT_ID w/WEEK s/STATUS", "attendance list w/WEEK",
            "participation set id/STUDENT_ID w/WEEK s/SCORE", "participation list w/WEEK");

    @TempDir
    public Path temporaryFolder;

    private MainApp app;

    @BeforeEach
    public void setUp() throws Exception {
        app = new MainApp(temporaryFolder.resolve("prefs.json"), temporaryFolder.resolve("tassist.json"));
        app.init();
    }

    @Test
    public void getHelpEntries_listsEveryCommandInHelpOrder() {
        assertEquals(EXPECTED_FORMATS, app.logic.getHelpEntries().stream().map(HelpEntry::getCommandFormat).toList());
    }

    @Test
    public void getHelpEntries_everyTopicWithCommands_hasExamplesOfItsOwnCommands() {
        for (String topic : List.of("group", "student", "attendance", "participation")) {
            List<HelpEntry> entries = HelpCatalog.filterByTopic(app.logic.getHelpEntries(), topic);
            assertFalse(entries.isEmpty(), "No help for " + topic);
            for (HelpEntry entry : entries) {
                String commandWords = getCommandWords(entry.getCommandFormat());
                assertTrue(entry.getExamples().stream().allMatch(example -> example.startsWith(commandWords + " ")
                        || example.equals(commandWords)), "An example of " + commandWords + " runs another command");
            }
        }
    }

    @Test
    public void examples_typedInHelpOrderIntoNewApp_allSucceed() {
        for (HelpEntry entry : app.logic.getHelpEntries()) {
            for (String example : entry.getExamples()) {
                assertDoesNotThrow(() -> app.logic.execute(example), "Help example failed: " + example);
            }
        }

        Group t01 = app.model.getGroup(new GroupName("T01"));
        assertTrue(app.model.hasGroup(new GroupName("T02")));
        assertEquals(t01, app.model.getActiveGroup().orElseThrow());
        assertTrue(t01.hasStudent(new StudentId("A0123456X")));
        assertFalse(t01.hasStudent(new StudentId("A0234567Y")));
    }

    /**
     * Returns the leading command words of {@code commandFormat}, such as {@code group add} for
     * {@code group add g/GROUP_NAME}. Command words contain lowercase letters only.
     */
    private static String getCommandWords(String commandFormat) {
        return Arrays.stream(commandFormat.split(" "))
                .takeWhile(word -> word.matches("[a-z]+"))
                .collect(Collectors.joining(" "));
    }
}
