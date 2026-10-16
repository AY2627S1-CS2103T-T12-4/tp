package seedu.address;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.AppHome;
import seedu.address.commons.core.DataLoadStatus;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.logic.parser.AttendanceCommandRegistration;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.logic.parser.GroupCommandRegistration;
import seedu.address.logic.parser.ParticipationCommandRegistration;
import seedu.address.logic.parser.StudentCommandRegistration;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyTAssist;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.storage.JsonTAssistStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V1.3";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = AppHome.getHomeDirectory().resolve("preferences.json");
    private static final Path TASSIST_FILE_PATH = AppHome.getHomeDirectory().resolve(Path.of("data", "tassist.json"));

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    private final Path userPrefsFilePath;
    private final Path tAssistFilePath;
    private DataLoadStatus dataLoadStatus = DataLoadStatus.LOADED;

    /**
     * Creates a TAssist application that uses the default preference and data file paths.
     */
    public MainApp() {
        this(USER_PREFS_FILE_PATH, TASSIST_FILE_PATH);
    }

    /**
     * Creates a TAssist application that reads preferences from {@code userPrefsFilePath}
     * and TAssist data from {@code tAssistFilePath}.
     */
    MainApp(Path userPrefsFilePath, Path tAssistFilePath) {
        requireNonNull(userPrefsFilePath);
        requireNonNull(tAssistFilePath);
        this.userPrefsFilePath = userPrefsFilePath;
        this.tAssistFilePath = tAssistFilePath;
    }

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing TAssist ]===============================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(userPrefsFilePath);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonTAssistStorage tAssistStorage = new JsonTAssistStorage(tAssistFilePath);
        storage = new StorageManager(tAssistStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        CommandRegistry commandRegistry = new CommandRegistry();
        GroupCommandRegistration.registerCommands(commandRegistry);
        AttendanceCommandRegistration.registerCommands(commandRegistry);
        ParticipationCommandRegistration.registerCommands(commandRegistry);
        StudentCommandRegistration.registerCommands(commandRegistry);
        logic = new LogicManager(model, storage, commandRegistry, dataLoadStatus);

        ui = new UiManager(logic, storage.getTAssistFilePath(), dataLoadStatus);
    }

    /**
     * Returns a {@code ModelManager} with the TAssist data from {@code storage} and {@code userPrefs}. <br>
     * An empty TAssist is used instead if {@code storage}'s data file is not found,
     * or if errors occur when reading it.
     */
    Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        Path dataFilePath = storage.getTAssistFilePath();
        logger.info("Using data file : " + dataFilePath);

        ReadOnlyTAssist initialData;
        try {
            Optional<ReadOnlyTAssist> tAssistOptional = storage.readTAssist();
            dataLoadStatus = tAssistOptional.isPresent() ? DataLoadStatus.LOADED : DataLoadStatus.MISSING;
            if (tAssistOptional.isEmpty()) {
                logger.info("Data file " + dataFilePath + " not found. Starting with an empty TAssist.");
            }
            initialData = tAssistOptional.orElseGet(TAssist::new);
        } catch (DataLoadingException e) {
            dataLoadStatus = DataLoadStatus.FAILED;
            logger.warning("Data file at " + dataFilePath + " could not be loaded: " + e.getCause().getMessage()
                    + " Starting with an empty TAssist.");
            initialData = new TAssist();
        }

        return new ModelManager(initialData, userPrefs);
    }

    DataLoadStatus getDataLoadStatus() {
        return dataLoadStatus;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting TAssist " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping TAssist ] =================================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
