package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.logic.parser.GroupCommandRegistration;
import seedu.address.logic.parser.Prefix;
import seedu.address.logic.parser.TAssistArgumentParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyTAssist;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.storage.JsonTAssistStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.TypicalGroups;

public class GroupCommandsIntegrationTest {
    @TempDir
    public Path temporaryFolder;

    private Model model;
    private Logic logic;
    private Path dataFile;

    @BeforeEach
    public void setUp() throws Exception {
        model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.addGroup(new Group(new GroupName("Tutorial 1")));
        model.setActiveGroup(TypicalGroups.NAME_T02);
        dataFile = temporaryFolder.resolve("tassist.json");
        new JsonTAssistStorage(dataFile).saveTAssist(model.getTAssist());
        JsonTAssistStorage storage = new JsonTAssistStorage(dataFile) {
            @Override
            public void saveTAssist(ReadOnlyTAssist tAssist) {
                throw new AssertionError("Rejected commands must not call storage.");
            }
        };
        CommandRegistry registry = new CommandRegistry();
        GroupCommandRegistration.registerCommands(registry);
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))), registry);
    }

    @Test
    public void execute_invalidGroupNames_preservesModelAndFile() throws Exception {
        for (String subcommand : List.of("add", "use")) {
            String command = "group " + subcommand;
            String missingName = "Missing group name. Usage: " + command + " g/GROUP_NAME";
            assertRejected(command, ParseException.class, missingName);
            assertRejected(command + " g/   ", ParseException.class, missingName);
            for (String name : List.of("T_01", "a".repeat(61), "--")) {
                assertRejected(command + " g/" + name, ParseException.class, GroupName.MESSAGE_CONSTRAINTS);
            }
        }
    }

    @Test
    public void execute_malformedArguments_preservesModelAndFile() throws Exception {
        for (String subcommand : List.of("add", "use")) {
            String command = "group " + subcommand;
            assertRejected(command + " g/T01 g/T02", ParseException.class,
                    Messages.getErrorMessageForDuplicatePrefixes(new Prefix("g/")));
            assertRejected(command + " n/T01", ParseException.class,
                    String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "n/", "g/"));
            assertRejected(command + " T01", ParseException.class,
                    String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "T01", "g/"));
        }
        assertRejected("group list g/T01", ParseException.class, TAssistArgumentParser.MESSAGE_NO_PARAMETERS);
        assertRejected("group", ParseException.class, String.format(CommandRegistry.MESSAGE_MISSING_SUBCOMMAND,
                "group", "add, list, use", "group"));
        assertRejected("group remove", ParseException.class, String.format(CommandRegistry.MESSAGE_UNKNOWN_SUBCOMMAND,
                "remove", "group", "add, list, use"));
    }

    @Test
    public void execute_duplicateAndUnknownGroups_preservesModelAndFile() throws Exception {
        assertRejected("group add g/t01", CommandException.class, "A tutorial group named T01 already exists.");
        assertRejected("group add g/ tutorial   1 ", CommandException.class,
                "A tutorial group named Tutorial 1 already exists.");
        assertRejected("group use g/T04", CommandException.class, "Tutorial group T04 does not exist.");
    }

    private void assertRejected(String command, Class<? extends Exception> exceptionType, String message)
            throws Exception {
        TAssist expected = new TAssist(model.getTAssist());
        String saved = Files.readString(dataFile);

        assertThrows(exceptionType, message, () -> logic.execute(command));

        assertEquals(expected, model.getTAssist());
        assertEquals(saved, Files.readString(dataFile));
        assertEquals(TypicalGroups.getT02().getStudentList(), logic.getActiveGroupStudentList());
        assertEquals("T02", logic.activeGroupProperty().get().orElseThrow().getName().toString());
    }
}
