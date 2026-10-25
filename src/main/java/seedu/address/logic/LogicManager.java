package seedu.address.logic;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.DataLoadStatus;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.logic.parser.TAssistParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.TAssist;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;
import seedu.address.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String MESSAGE_SAVE_FAILURE =
            "Unable to save changes. Your previous saved data is unchanged.";

    public static final String MESSAGE_SAVE_PERMISSION_FAILURE = MESSAGE_SAVE_FAILURE
            + " Please check that the application has permission to write to its data folder.";

    public static final String MESSAGE_DATA_PROTECTED = DataLoadStatus.FAILED.getMessage()
            + " Close TAssist, repair or move the unreadable data file, and restart before making changes.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final TAssistParser tAssistParser;
    private final DataLoadStatus dataLoadStatus;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this(model, storage, new CommandRegistry());
    }

    /**
     * Constructs a logic manager with feature commands registered before startup.
     *
     * @param model The application model.
     * @param storage The local persistence component.
     * @param commandRegistry The feature command registrations.
     */
    public LogicManager(Model model, Storage storage, CommandRegistry commandRegistry) {
        this(model, storage, commandRegistry, DataLoadStatus.LOADED);
    }

    /**
     * Constructs a logic manager that protects saved data if startup loading failed.
     *
     * @param model The application model.
     * @param storage The local persistence component.
     * @param commandRegistry The feature command registrations.
     * @param dataLoadStatus The startup data-loading outcome.
     */
    public LogicManager(Model model, Storage storage, CommandRegistry commandRegistry, DataLoadStatus dataLoadStatus) {
        this.model = requireNonNull(model);
        this.storage = requireNonNull(storage);
        this.dataLoadStatus = requireNonNull(dataLoadStatus);
        tAssistParser = new TAssistParser(commandRegistry);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Command command = tAssistParser.parseCommand(commandText);
        TAssist previousData = new TAssist(model.getTAssist());
        try {
            return executeAndSave(command, previousData);
        } catch (CommandException e) {
            model.setTAssist(previousData);
            assert previousData.equals(model.getTAssist()) : "A failed command must leave data unchanged";
            throw e;
        }
    }

    @Override
    public Optional<String> getConfirmationPrompt(String commandText) throws ParseException {
        requireNonNull(commandText);
        return tAssistParser.parseCommand(commandText).getConfirmationPrompt(model);
    }

    private CommandResult executeAndSave(Command command, TAssist previousData) throws CommandException {
        CommandResult commandResult = command.execute(model);

        if (previousData.equals(model.getTAssist())) {
            return commandResult;
        }

        if (dataLoadStatus.isFailure()) {
            throw new CommandException(MESSAGE_DATA_PROTECTED);
        }

        saveData();
        return commandResult.withSavedChanges();
    }

    private void saveData() throws CommandException {
        try {
            storage.saveTAssist(model.getTAssist());
        } catch (AccessDeniedException e) {
            logger.warning("Saving data was denied: " + e);
            throw new CommandException(MESSAGE_SAVE_PERMISSION_FAILURE, e);
        } catch (IOException ioe) {
            logger.warning("Saving data failed: " + ioe);
            throw new CommandException(MESSAGE_SAVE_FAILURE, ioe);
        }

    }

    @Override
    public List<HelpEntry> getHelpEntries() {
        return tAssistParser.getHelpEntries();
    }

    @Override
    public ObservableList<Group> getGroupList() {
        return model.getTAssist().getGroupList();
    }

    @Override
    public ReadOnlyObjectProperty<Optional<Group>> activeGroupProperty() {
        return model.activeGroupProperty();
    }

    @Override
    public ObservableList<Student> getActiveGroupStudentList() {
        return model.getActiveGroupStudentList();
    }

    @Override
    public ObservableList<Student> getDisplayedStudentList() {
        return model.getDisplayedStudentList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
