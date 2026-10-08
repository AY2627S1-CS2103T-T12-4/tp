package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.core.WorkspaceView;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.parser.ArgumentMultimap;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.logic.parser.Prefix;
import seedu.address.logic.parser.TAssistArgumentParser;
import seedu.address.logic.parser.TAssistParserUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.student.StudentId;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.HelpEntryBuilder;
import seedu.address.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonAddressBookStorage addressBookStorage =
                new JsonAddressBookStorage(temporaryFolder.resolve("addressBook.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, String.format(MESSAGE_UNKNOWN_COMMAND, invalidCommand));
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_view_doesNotCreateDataFile() throws Exception {
        assertEquals(WorkspaceView.ATTENDANCE, logic.execute("view attendance").getView().orElseThrow());
        assertFalse(Files.exists(temporaryFolder.resolve("addressBook.json")));
    }

    @Test
    public void execute_viewWithUnavailableStorage_success() throws Exception {
        JsonAddressBookStorage failingStorage = new JsonAddressBookStorage(
                temporaryFolder.resolve("unavailable.json")) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw DUMMY_AD_EXCEPTION;
            }
        };
        logic = new LogicManager(model, new StorageManager(failingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))));
        assertEquals(WorkspaceView.HELP, logic.execute("view help").getView().orElseThrow());
    }

    @Test
    public void execute_help_returnsCommandReferenceInFeedback() throws Exception {
        CommandRegistry registry = new CommandRegistry();
        registry.register("student", "list", args -> new ListCommand(),
                new HelpEntryBuilder().withCommandFormat("student list").build());
        logic = new LogicManager(model, new StorageManager(new JsonAddressBookStorage(
                temporaryFolder.resolve("help.json")), new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))),
                registry);

        CommandResult all = logic.execute("help");
        assertTrue(all.isShowHelp());
        assertEquals(HelpCatalog.format(logic.getHelpEntries()), all.getFeedbackToUser());

        CommandResult topic = logic.execute("help student");
        assertTrue(topic.isShowHelp());
        assertEquals(HelpCatalog.format(List.of(logic.getHelpEntries().getLast())), topic.getFeedbackToUser());
    }

    @Test
    public void execute_helpUnknownTopic_throwsParseException() {
        assertThrows(ParseException.class, HelpCommand.MESSAGE_UNKNOWN_TOPIC, () -> logic.execute("help xyz"));
    }

    @Test
    public void execute_registeredCommand_validatesExecutesAndSaves() throws Exception {
        Path dataFile = temporaryFolder.resolve("feature.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(dataFile),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        logic = new LogicManager(model, storage, createFeatureRegistry());

        Person expectedPerson = new PersonBuilder().withName("Alice Tan").build();
        Model expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandSuccess(" student   add id/a1 n/ Alice   Tan ",
                String.format(AddCommand.MESSAGE_SUCCESS, Messages.format(expectedPerson)), expectedModel);
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void execute_invalidFeatureCommands_doesNotExecuteOrSave() throws Exception {
        model.addPerson(AMY);
        Path dataFile = temporaryFolder.resolve("unchanged.json");
        String existingData = "Existing file contents must not be overwritten.";
        Files.writeString(dataFile, existingData);
        JsonAddressBookStorage saveDetectingStorage = new JsonAddressBookStorage(dataFile) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) {
                throw new AssertionError("Invalid input must not call storage.");
            }
        };
        logic = new LogicManager(model, new StorageManager(saveDetectingStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json"))), createFeatureRegistry());

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
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
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
            String name = TAssistParserUtil.parseStudentName(values.getValue(namePrefix).orElseThrow()).fullName;
            TAssistParserUtil.parseStudentId(values.getValue(idPrefix).orElseThrow());
            return new AddCommand(new PersonBuilder().withName(name).build());
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
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
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
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonAddressBookStorage that throws the IOException e when saving
        JsonAddressBookStorage addressBookStorage = new JsonAddressBookStorage(prefPath) {
            @Override
            public void saveAddressBook(ReadOnlyAddressBook addressBook) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(addressBookStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveAddressBook method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        expectedModel.addPerson(expectedPerson);
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
    }
}
