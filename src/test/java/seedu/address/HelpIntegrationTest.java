package seedu.address;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
 * Checks the help of the running app: that it covers every command, that its examples work when typed, and that
 * the User Guide shows the same formats and examples.
 */
public class HelpIntegrationTest {
    /** Every command format, in the order the Help screen shows them. */
    private static final List<String> EXPECTED_FORMATS = List.of(
            "view SCREEN", "help [TOPIC]", "undo", "redo", "exit",
            "group add g/GROUP_NAME", "group list", "group use g/GROUP_NAME", "group export",
            "student add n/NAME id/STUDENT_ID", "student list", "student delete id/STUDENT_ID [f/yes]",
            "attendance mark id/STUDENT_ID w/WEEK s/STATUS", "attendance list w/WEEK",
            "participation set id/STUDENT_ID w/WEEK s/SCORE", "participation list w/WEEK",
            "assignment add a/ASSIGNMENT_NAME", "assignment submit a/ASSIGNMENT_NAME id/STUDENT_ID s/STATUS",
            "assignment grade a/ASSIGNMENT_NAME id/STUDENT_ID m/GRADE", "assignment list a/ASSIGNMENT_NAME");

    private static final Path USER_GUIDE = Path.of("docs", "UserGuide.md");
    private static final String COMMAND_SUMMARY_HEADING = "## Command summary";
    /** An example in the command summary table, such as {@code e.g., `group add g/T01`}. */
    private static final Pattern SUMMARY_EXAMPLE = Pattern.compile("e\\.g\\., `([^`]+)`");

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
        for (String topic : HelpCatalog.TOPICS) {
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

    @Test
    public void userGuide_showsEveryHelpFormatAndExample() throws Exception {
        String userGuide = Files.readString(USER_GUIDE);
        for (HelpEntry entry : app.logic.getHelpEntries()) {
            assertTrue(userGuide.contains("`" + entry.getCommandFormat() + "`"),
                    "The User Guide does not show the format " + entry.getCommandFormat());
            for (String example : entry.getExamples()) {
                assertTrue(userGuide.contains("`" + example + "`"), "The User Guide does not show " + example);
            }
        }
    }

    @Test
    public void userGuideCommandSummary_usesOnlyHelpExamples() throws Exception {
        String userGuide = Files.readString(USER_GUIDE);
        String commandSummary = userGuide.substring(userGuide.indexOf(COMMAND_SUMMARY_HEADING));
        List<String> helpExamples = app.logic.getHelpEntries().stream()
                .flatMap(entry -> entry.getExamples().stream()).toList();

        Matcher summaryExamples = SUMMARY_EXAMPLE.matcher(commandSummary);
        int exampleCount = 0;
        while (summaryExamples.find()) {
            exampleCount++;
            assertTrue(helpExamples.contains(summaryExamples.group(1)),
                    "The command summary example is not a help example: " + summaryExamples.group(1));
        }
        assertTrue(exampleCount > 0, "The command summary has no examples");
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
