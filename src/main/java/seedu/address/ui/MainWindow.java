package seedu.address.ui;

import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.WorkspaceView;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.Group;

/**
 * Connects the TAssist workspace, command input and feedback to application logic.
 */
public class MainWindow extends UiPart<Stage> {
    private static final String FXML = "MainWindow.fxml";
    private static final PseudoClass NO_GROUP = PseudoClass.getPseudoClass("no-group");
    private final Logger logger = LogsCenter.getLogger(getClass());
    private final Stage primaryStage;
    private final Logic logic;
    private final Path dataFilePath;

    private WorkspacePanel workspacePanel;
    private ResultDisplay resultDisplay;
    private CommandBox commandBox;
    private StatusBarFooter statusBarFooter;

    @FXML
    private StackPane commandBoxPlaceholder;
    @FXML
    private MenuItem helpMenuItem;
    @FXML
    private Label activeGroupCaption;
    @FXML
    private Label activeGroupLabel;
    @FXML
    private StackPane groupStripPlaceholder;
    @FXML
    private StackPane workspacePlaceholder;
    @FXML
    private StackPane resultDisplayPlaceholder;
    @FXML
    private StackPane statusbarPlaceholder;

    /**
     * Creates the main window with its logic and configured data path.
     */
    public MainWindow(Stage primaryStage, Logic logic, Path dataFilePath) {
        super(FXML, primaryStage);
        this.primaryStage = primaryStage;
        this.logic = logic;
        this.dataFilePath = dataFilePath;

        setWindowDefaultSize(logic.getGuiSettings());
        setAccelerators();

        Region workspace = (Region) primaryStage.getScene().getRoot();
        workspace.heightProperty().addListener((observable, oldHeight, height) ->
                workspace.pseudoClassStateChanged(PseudoClass.getPseudoClass("compact"), height.doubleValue() < 540));
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private void setAccelerators() {
        helpMenuItem.setAccelerator(KeyCombination.valueOf("F1"));
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F1) {
                handleHelp();
                event.consume();
            } else if (event.getCode() == KeyCode.ESCAPE) {
                commandBox.focus();
                event.consume();
            }
        });
    }

    /**
     * Fills the window with its independent UI parts.
     */
    void fillInnerParts() {
        showActiveGroup(logic.activeGroupProperty());
        GroupStrip groupStrip = new GroupStrip(logic.getGroupList(), logic.activeGroupProperty());
        groupStripPlaceholder.getChildren().add(groupStrip.getRoot());

        workspacePanel = new WorkspacePanel(logic.activeGroupProperty(), logic.getActiveGroupStudentList(),
                dataFilePath, logic.getHelpEntries());
        workspacePlaceholder.getChildren().add(workspacePanel.getRoot());

        resultDisplay = new ResultDisplay();
        resultDisplayPlaceholder.getChildren().add(resultDisplay.getRoot());

        statusBarFooter = new StatusBarFooter(dataFilePath);
        statusbarPlaceholder.getChildren().add(statusBarFooter.getRoot());

        commandBox = new CommandBox(this::executeCommand);
        commandBoxPlaceholder.getChildren().add(commandBox.getRoot());
        Platform.runLater(commandBox::focus);
    }

    /**
     * Keeps the header showing the name of {@code activeGroup}, with a tooltip for names too long to fit.
     * When no group is active, the header says so in the same words as the Students screen.
     */
    private void showActiveGroup(ObservableValue<Optional<Group>> activeGroup) {
        activeGroupLabel.textProperty().bind(Bindings.createStringBinding(() ->
                activeGroup.getValue().map(group -> group.getName().toString())
                        .orElse(StudentListPanel.NO_ACTIVE_GROUP),
                activeGroup));
        BooleanBinding hasActiveGroup = Bindings.createBooleanBinding(() -> activeGroup.getValue().isPresent(),
                activeGroup);
        activeGroupCaption.visibleProperty().bind(hasActiveGroup);
        activeGroupCaption.managedProperty().bind(hasActiveGroup);
        activeGroupLabel.pseudoClassStateChanged(NO_GROUP, !hasActiveGroup.get());
        hasActiveGroup.addListener((observable, hadGroup, hasGroup) ->
                activeGroupLabel.pseudoClassStateChanged(NO_GROUP, !hasGroup));
        Tooltip fullName = new Tooltip();
        fullName.textProperty().bind(activeGroupLabel.textProperty());
        activeGroupLabel.setTooltip(fullName);
    }

    private void setWindowDefaultSize(GuiSettings guiSettings) {
        var bounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setHeight(Math.min(guiSettings.getWindowHeight(), bounds.getHeight()));
        primaryStage.setWidth(Math.min(guiSettings.getWindowWidth(), bounds.getWidth()));
        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(guiSettings.getWindowCoordinates().getY());
        }
    }

    /**
     * Shows the inline command reference.
     */
    @FXML
    public void handleHelp() {
        workspacePanel.showView(WorkspaceView.HELP);
        commandBox.focus();
    }

    /**
     * Opens Help if {@code result} asks for it. Other commands keep the current screen, so a contact command
     * does not jump to the Students screen, where contacts are no longer shown.
     */
    private void showHelpIfRequested(CommandResult result) {
        if (result.isShowHelp()) {
            workspacePanel.showView(WorkspaceView.HELP);
        }
    }

    void show() {
        primaryStage.show();
    }

    /**
     * Saves window preferences and closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
        logic.setGuiSettings(guiSettings);
        primaryStage.hide();
    }

    /**
     * Executes a command and displays its result without clearing failed input.
     */
    private CommandResult executeCommand(String commandText) throws CommandException, ParseException {
        try {
            CommandResult result = logic.execute(commandText);
            logger.info("Result: " + result.getFeedbackToUser());
            resultDisplay.setFeedbackToUser(result.getFeedbackToUser());
            if (result.getView().isPresent()) {
                workspacePanel.showView(result.getView().get());
                statusBarFooter.setStatus("Local data file");
            } else {
                statusBarFooter.setStatus("Changes saved");
                showHelpIfRequested(result);
            }
            if (result.isExit()) {
                handleExit();
            }
            return result;
        } catch (CommandException | ParseException e) {
            logger.info("Command failed: " + e.getMessage());
            resultDisplay.setFeedbackToUser(e.getMessage(), true);
            statusBarFooter.setStatus("Command failed — see feedback");
            throw e;
        }
    }
}
