package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.WorkspaceView;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Phone;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;

/**
 * Exercises the real JavaFX views on a desktop when tassist.uiTests is enabled.
 */
@EnabledIfSystemProperty(named = "tassist.uiTests", matches = "true")
public class WorkspaceSmokeTest {
    @TempDir
    public Path temporaryFolder;

    private Stage stage;
    private ModelManager model;
    private Parent root;
    private TextField input;
    private TabPane tabs;
    private Path data;

    @BeforeAll
    public static void startToolkit() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> {
            Platform.setImplicitExit(false);
            return null;
        });
        Platform.startup(startup);
        startup.get(20, TimeUnit.SECONDS);
    }

    @AfterAll
    public static void stopToolkit() {
        Platform.exit();
    }

    @Test
    public void workspace_navigationCommandsAndResizing_rendersAndPreservesBehavior() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            verifyScreens();
            verifyKeyboard();
            assertFalse(Files.exists(data), "Screen navigation must not write contact data");
            verifyRoster();
            runOnFx(() -> render(root, "long-values", 853, 440));
            verifyFailure();
            runOnFx(() -> render(root, "error", 853, 440));
            runOnFx(() -> {
                enter(input, "view storage");
                Label status = (Label) root.lookup("#saveLocationStatus");
                assertTrue(status.getText().startsWith("Local data file"));
                enter(input, "find MissingPerson");
                assertTrue(model.getFilteredPersonList().isEmpty());
            });
            runOnFx(() -> render(root, "empty-roster", 853, 440));
            runOnFx(() -> enter(input, "list"));
            runOnFx(() -> render(root, "students-large", 1920, 1040));
            runOnFx(() -> render(root, "students-medium", 1536, 824));
            runOnFx(() -> render(root, "students-laptop", 1280, 680));
        } finally {
            runOnFx(() -> stage.hide());
        }
    }

    @Test
    public void exitCommand_savesWindowSettingsAndClosesWindow() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                double width = stage.getWidth();
                double height = stage.getHeight();

                enter(input, "exit");

                assertFalse(stage.isShowing());
                assertEquals(width, model.getGuiSettings().getWindowWidth());
                assertEquals(height, model.getGuiSettings().getWindowHeight());
            });
        } finally {
            runOnFx(() -> stage.hide());
        }
    }

    private void verifyScreens() throws Exception {
        for (WorkspaceView view : WorkspaceView.values()) {
            runOnFx(() -> {
                enter(input, "view " + view.getKeyword());
                assertEquals(view.getKeyword(), tabs.getSelectionModel().getSelectedItem().getId());
                assertTrue(input.getText().isEmpty());
            });
            runOnFx(() -> render(root, view.getKeyword(), 960, 640));
            runOnFx(() -> render(root, view.getKeyword() + "-compact", 853, 440));
        }
    }

    private void verifyKeyboard() throws Exception {
        runOnFx(() -> {
            input.setText("find Alex");
            input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1, false, false, false, false));
            assertEquals("help", tabs.getSelectionModel().getSelectedItem().getId());
            assertEquals("find Alex", input.getText());
            tabs.requestFocus();
            tabs.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.ESCAPE, false, false, false, false));
            assertEquals(input, stage.getScene().getFocusOwner());
        });
    }

    private void verifyRoster() throws Exception {
        runOnFx(() -> {
            TextArea feedbackArea = (TextArea) root.lookup("#resultDisplay");
            enter(input, "help");
            assertEquals("help", tabs.getSelectionModel().getSelectedItem().getId());
            assertTrue(feedbackArea.getText().contains("Student commands\nstudent list"));
            assertTrue(feedbackArea.getText().contains("view SCREEN"));
            enter(input, "help student");
            assertTrue(feedbackArea.getText().contains("Student commands\nstudent list"));
            assertFalse(feedbackArea.getText().contains("view SCREEN"));
            enter(input, "find Alex");
            assertEquals("students", tabs.getSelectionModel().getSelectedItem().getId());
            assertEquals(1, model.getFilteredPersonList().size());
            enter(input, "view help");
            VBox commands = (VBox) root.lookup("#commands");
            assertTrue(commands.getChildren().stream().anyMatch(node -> node instanceof Label label
                    && label.getText().equals("Student commands")));
            enter(input, "view students");
            assertEquals(1, model.getFilteredPersonList().size());
            enter(input, "list");
            int originalSize = model.getFilteredPersonList().size();
            enter(input, "add n/" + "Longname ".repeat(30).trim()
                    + " p/98765432 e/long@example.com a/" + "Long address ".repeat(40));
            assertEquals(originalSize + 1, model.getFilteredPersonList().size());
        });
    }

    private void verifyFailure() throws Exception {
        runOnFx(() -> {
            enter(input, "delete 1");
            TableView<?> table = (TableView<?>) root.lookup("#personTable");
            assertEquals("1", table.getColumns().getFirst().getCellObservableValue(0).getValue());
            enter(input, "edit 1 p/invalid");
            assertEquals("edit 1 p/invalid", input.getText());
            assertTrue(input.getStyleClass().contains("error"));
            TextArea feedback = (TextArea) root.lookup("#resultDisplay");
            assertEquals(Phone.MESSAGE_CONSTRAINTS, feedback.getText());
        });
    }

    private void initializeWorkspace() {
        model = new ModelManager(SampleDataUtil.getSampleAddressBook(), new UserPrefs());
        data = temporaryFolder.resolve("contacts.json");
        StorageManager storage = new StorageManager(new JsonAddressBookStorage(data),
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        CommandRegistry commandRegistry = new CommandRegistry();
        commandRegistry.register("student", "list", args -> new ListCommand(),
                new HelpEntry("student", "student list", "Show all students.", List.of("student list")));
        stage = new Stage();
        MainWindow window = new MainWindow(stage, new LogicManager(model, storage, commandRegistry), data);
        window.fillInnerParts();
        window.show();
        root = stage.getScene().getRoot();
        root.applyCss();
        input = (TextField) root.lookup("#commandTextField");
        tabs = (TabPane) root.lookup("#tabs");
        assertNotNull(input);
        assertEquals(7, tabs.getTabs().size());
    }

    private void runOnFx(CheckedAction action) throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            action.run();
            return null;
        });
        Platform.runLater(check);
        check.get(20, TimeUnit.SECONDS);
    }

    /**
     * Allows a JavaFX test step to report checked failures to the test thread.
     */
    @FunctionalInterface
    private interface CheckedAction {
        void run() throws Exception;
    }

    private void enter(TextField input, String command) {
        input.setText(command);
        input.fireEvent(new ActionEvent());
    }

    private void render(Parent root, String name, int width, int height) throws Exception {
        root.resize(width, height);
        root.applyCss();
        root.layout();
        WritableImage image = root.snapshot(null, null);
        BufferedImage output = new BufferedImage((int) image.getWidth(), (int) image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < output.getHeight(); y++) {
            for (int x = 0; x < output.getWidth(); x++) {
                output.setRGB(x, y, image.getPixelReader().getArgb(x, y));
            }
        }
        Path directory = Path.of("build", "ui-previews");
        Files.createDirectories(directory);
        ImageIO.write(output, "png", directory.resolve(name + ".png").toFile());
    }
}
