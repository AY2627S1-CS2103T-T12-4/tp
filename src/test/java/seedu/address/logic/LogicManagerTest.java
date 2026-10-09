package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.FIONA;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.WorkspaceView;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.logic.parser.Prefix;
import seedu.address.logic.parser.TAssistArgumentParser;
import seedu.address.logic.parser.TAssistParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyTAssist;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.storage.JsonTAssistStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.HelpEntryBuilder;
import seedu.address.testutil.StubCommand;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        logic = new LogicManager(model, createStorage(new JsonTAssistStorage(temporaryFolder.resolve("tassist.json"))));
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, String.format(MESSAGE_UNKNOWN_COMMAND, invalidCommand));
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String failureMessage = "The command failed.";
        CommandRegistry registry = new CommandRegistry();
        registry.register("student", "fail", args -> StubCommand.failingWith(failureMessage));
        logic = new LogicManager(model, createStorage(new JsonTAssistStorage(temporaryFolder.resolve("fail.json"))),
                registry);

        assertCommandException("student fail", failureMessage);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        CommandRegistry registry = new CommandRegistry();
        registry.register("student", "list", args -> new StubCommand());
        logic = new LogicManager(model, createStorage(new JsonTAssistStorage(temporaryFolder.resolve("ok.json"))),
                registry);

        assertCommandSuccess("student list", StubCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_view_doesNotCreateDataFile() throws Exception {
        assertEquals(WorkspaceView.ATTENDANCE, logic.execute("view attendance").getView().orElseThrow());
        assertFalse(Files.exists(temporaryFolder.resolve("tassist.json")));
    }

    @Test
    public void execute_viewWithUnavailableStorage_success() throws Exception {
        logic = new LogicManager(model, createStorage(createFailingTAssistStorage(DUMMY_AD_EXCEPTION)));
        assertEquals(WorkspaceView.HELP, logic.execute("view help").getView().orElseThrow());
    }

    @Test
    public void execute_help_opensHelpWithShortConfirmation() throws Exception {
        CommandRegistry registry = new CommandRegistry();
        registry.register("student", "list", args -> new StubCommand(),
                new HelpEntryBuilder().withCommandFormat("student list").build());
        logic = new LogicManager(model, createStorage(new JsonTAssistStorage(temporaryFolder.resolve("help.json"))),
                registry);

        CommandResult all = logic.execute("help");
        assertEquals(CommandResult.forHelp(HelpCommand.MESSAGE_SHOWING_HELP, ""), all);

        CommandResult topic = logic.execute("help student");
        assertEquals(CommandResult.forHelp("Showing student commands.", "student"), topic);
    }

    @Test
    public void execute_helpUnknownTopic_throwsParseException() {
        assertThrows(ParseException.class, HelpCommand.MESSAGE_UNKNOWN_TOPIC, () -> logic.execute("help xyz"));
    }

    @Test
    public void execute_registeredCommand_validatesExecutesAndSaves() throws Exception {
        TAssist tAssist = new TAssistBuilder(TypicalGroups.getTypicalTAssist())
                .withActiveGroup(TypicalGroups.NAME_T02).build();
        model.setTAssist(tAssist);
        StorageManager storage = createStorage(new JsonTAssistStorage(temporaryFolder.resolve("feature.json")));
        logic = new LogicManager(model, storage, createFeatureRegistry());

        Model expectedModel = new ModelManager(tAssist, new UserPrefs());
        expectedModel.addStudent(TypicalGroups.NAME_T02,
                new Student(new StudentName("Alice Tan"), new StudentId("a1")));
        assertCommandSuccess(" student   add id/a1 n/ Alice   Tan ", StubCommand.MESSAGE_SUCCESS, expectedModel);
        assertEquals(new TAssist(expectedModel.getTAssist()), new TAssist(storage.readTAssist().orElseThrow()));
    }

    @Test
    public void execute_invalidFeatureCommands_doesNotExecuteOrSave() throws Exception {
        Path dataFile = temporaryFolder.resolve("unchanged.json");
        String existingData = "Existing file contents must not be overwritten.";
        Files.writeString(dataFile, existingData);
        JsonTAssistStorage saveDetectingStorage = new JsonTAssistStorage(dataFile) {
            @Override
            public void saveTAssist(ReadOnlyTAssist tAssist) {
                throw new AssertionError("Invalid input must not call storage.");
            }
        };
        logic = new LogicManager(model, createStorage(saveDetectingStorage), createFeatureRegistry());

        assertParseException("unknown add", String.format(MESSAGE_UNKNOWN_COMMAND, "unknown"));
        assertParseException("student", String.format(CommandRegistry.MESSAGE_MISSING_SUBCOMMAND,
                "student", "add", "student"));
        assertParseException("student unknown", String.format(CommandRegistry.MESSAGE_UNKNOWN_SUBCOMMAND,
                "unknown", "student", "add"));
        assertParseException("student add id/A1", String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS,
                "n/"));
        assertParseException("student add n/Alice n/Ben id/A1",
                Messages.getErrorMessageForDuplicatePrefixes(new Prefix("n/")));
        assertParseException("student add n/Alice id/A1 x/value",
                String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "x/", "n/ id/"));
        assertParseException("student add n/Alice id/ABC", StudentId.MESSAGE_CONSTRAINTS);
        assertEquals(existingData, Files.readString(dataFile));
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getGroupList_groupAdded_showsGroup() {
        model.setTAssist(TypicalGroups.getTypicalTAssist());
        assertEquals(TypicalGroups.getTypicalGroups(), logic.getGroupList());

        model.addGroup(new Group(TypicalGroups.NAME_T04));

        assertTrue(logic.getGroupList().contains(new Group(TypicalGroups.NAME_T04)));
    }

    @Test
    public void getGroupList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getGroupList().add(TypicalGroups.getT01()));
    }

    @Test
    public void activeGroupProperty_groupBecomesActive_showsActiveGroup() {
        model.setTAssist(TypicalGroups.getTypicalTAssist());
        assertEquals(Optional.empty(), logic.activeGroupProperty().get());

        model.setActiveGroup(TypicalGroups.NAME_T02);

        assertEquals(Optional.of(TypicalGroups.getT02()), logic.activeGroupProperty().get());
    }

    @Test
    public void getActiveGroupStudentList_groupBecomesActive_showsItsStudents() {
        model.setTAssist(TypicalGroups.getTypicalTAssist());
        assertTrue(logic.getActiveGroupStudentList().isEmpty());

        model.setActiveGroup(TypicalGroups.NAME_T02);

        assertEquals(TypicalGroups.getT02().getStudentList(), logic.getActiveGroupStudentList());
    }

    @Test
    public void getActiveGroupStudentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getActiveGroupStudentList().add(FIONA));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    private CommandRegistry createFeatureRegistry() {
        Prefix namePrefix = new Prefix("n/");
        Prefix idPrefix = new Prefix("id/");
        TAssistArgumentParser argumentParser = new TAssistArgumentParser(List.of(namePrefix, idPrefix), List.of());
        CommandRegistry registry = new CommandRegistry();
        registry.register("student", "add", args -> {
            ArgumentMultimap values = argumentParser.parse(args);
            Student student = new Student(
                    TAssistParserUtil.parseStudentName(values.getValue(namePrefix).orElseThrow()),
                    TAssistParserUtil.parseStudentId(values.getValue(idPrefix).orElseThrow()));
            return new StubCommand(model -> model.addStudent(TypicalGroups.NAME_T02, student));
        });
        return registry;
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getTAssist(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        // Inject LogicManager with a JsonTAssistStorage that throws the IOException e when saving,
        // and trigger saveTAssist by executing a command that does not change the view
        CommandRegistry registry = new CommandRegistry();
        registry.register("student", "list", args -> new StubCommand());
        logic = new LogicManager(model, createStorage(createFailingTAssistStorage(e)), registry);
        assertCommandFailure("student list", CommandException.class, expectedMessage, new ModelManager());
    }

    /**
     * Returns a {@code StorageManager} that keeps TAssist data in {@code tAssistStorage},
     * and user prefs in a temporary file.
     */
    private StorageManager createStorage(JsonTAssistStorage tAssistStorage) {
        return new StorageManager(tAssistStorage, new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json")));
    }

    /**
     * Returns a {@code JsonTAssistStorage} that throws {@code e} whenever it is asked to save.
     */
    private JsonTAssistStorage createFailingTAssistStorage(IOException e) {
        return new JsonTAssistStorage(temporaryFolder.resolve("unavailable.json")) {
            @Override
            public void saveTAssist(ReadOnlyTAssist tAssist) throws IOException {
                throw e;
            }
        };
    }
}
