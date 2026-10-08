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
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
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
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.model.ModelManager;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Phone;
import seedu.address.model.student.Student;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.JsonTAssistStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TypicalGroups;

/**
 * Exercises the real JavaFX views on a desktop when tassist.uiTests is enabled.
 */
@EnabledIfSystemProperty(named = "tassist.uiTests", matches = "true")
public class WorkspaceSmokeTest {
    /** Number of repeats that brings a word near the 60-character limit of group and student names. */
    private static final int LONG_NAME_REPEATS = 6;

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
            assertFalse(Files.exists(data), "Screen navigation must not write TAssist data");
            verifyCommands();
            verifyFailure();
            runOnFx(() -> render(root, "error", 853, 440));
            runOnFx(() -> enter(input, "add"));
            runOnFx(() -> {
                render(root, "error-usage", 1280, 680);
                TextArea feedback = (TextArea) root.lookup("#resultDisplay");
                assertTrue(feedback.getText().contains(AddCommand.MESSAGE_USAGE));
                assertFalse(hasVisibleVerticalScrollBar(feedback), "A usage message fits without scrolling");
            });
            runOnFx(() -> {
                enter(input, "view storage");
                Label status = (Label) root.lookup("#saveLocationStatus");
                assertTrue(status.getText().startsWith("Local data file"));
            });
            runOnFx(() -> {
                model.setActiveGroup(TypicalGroups.NAME_T01);
                enter(input, "view students");
            });
            runOnFx(() -> render(root, "students-large", 1920, 1040));
            runOnFx(() -> render(root, "students-medium", 1536, 824));
            runOnFx(() -> render(root, "students-laptop", 1280, 680));
        } finally {
            runOnFx(() -> stage.hide());
        }
    }

    @Test
    public void studentsScreen_activeGroupChanges_showsActiveGroupAndItsStudents() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                assertStudentsShown(StudentListPanel.NO_ACTIVE_GROUP);
                assertEmptyTitle(StudentListPanel.NO_ACTIVE_GROUP);
                assertGroupChips(List.of("T01", "T02", "T03"), "");
            });
            runOnFx(() -> render(root, "no-active-group", 853, 440));

            runOnFx(() -> {
                model.setActiveGroup(TypicalGroups.NAME_T01);
                assertStudentsShown("T01");
                assertGroupChips(List.of("T01", "T02", "T03"), "T01");
                assertEquals("1", studentTable().getColumns().getFirst().getCellObservableValue(0).getValue());
            });

            runOnFx(() -> {
                model.setActiveGroup(TypicalGroups.NAME_T03);
                assertStudentsShown("T03");
                assertEmptyTitle(String.format(StudentListPanel.EMPTY_GROUP_TITLE_FORMAT, "T03"));
            });
            runOnFx(() -> render(root, "empty-group", 853, 440));

            runOnFx(() -> {
                model.addStudent(TypicalGroups.NAME_T03, new StudentBuilder().build());
                assertStudentsShown("T03");
                assertEquals(1, studentTable().getItems().size());
            });

            runOnFx(() -> {
                model.setTAssist(new TAssist());
                assertStudentsShown(StudentListPanel.NO_ACTIVE_GROUP);
                assertGroupChips(List.of(), "");
                assertTrue(root.lookupAll(".muted").stream().anyMatch(node -> node instanceof Label label
                        && label.getText().equals(GroupStrip.NO_GROUPS)));
            });
            runOnFx(() -> render(root, "no-groups", 853, 440));
        } finally {
            runOnFx(() -> stage.hide());
        }
    }

    @Test
    public void studentsScreen_longNames_keepsLayoutUsable() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                GroupName longGroupName = new GroupName("Longgroup".repeat(LONG_NAME_REPEATS));
                model.addGroup(new Group(longGroupName));
                model.addStudent(longGroupName, new StudentBuilder().withName("Longname".repeat(LONG_NAME_REPEATS))
                        .withStudentId("A".repeat(19) + "1").build());
                model.setActiveGroup(longGroupName);
                Label title = (Label) root.lookup("#groupTitle");
                assertEquals(longGroupName.toString(), title.getTooltip().getText());
            });
            runOnFx(() -> render(root, "long-values", 853, 440));
        } finally {
            runOnFx(() -> stage.hide());
        }
    }

    @Test
    public void navigationBar_clickAndCommand_openScreenAndKeepCommandFocus() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                assertNavigationHighlights("students");
                Button attendanceButton = (Button) root.lookup("#nav-attendance");
                attendanceButton.fire();
                assertEquals("attendance", tabs.getSelectionModel().getSelectedItem().getId());
                assertNavigationHighlights("attendance");
                assertEquals(input, stage.getScene().getFocusOwner(), "Clicking a button keeps the command focus");

                enter(input, "view storage");
                assertNavigationHighlights("storage");
            });
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

    private void verifyCommands() throws Exception {
        runOnFx(() -> {
            TextArea feedbackArea = (TextArea) root.lookup("#resultDisplay");
            ScrollPane helpScrollPane = (ScrollPane) root.lookup("#helpScrollPane");
            enter(input, "help student");
            assertEquals("help", tabs.getSelectionModel().getSelectedItem().getId());
            assertEquals("Showing student commands.", feedbackArea.getText());
            assertTrue(helpScrollPane.getVvalue() > helpScrollPane.getVmin(), "Help scrolls to the topic");
            enter(input, "help");
            assertEquals(HelpCommand.MESSAGE_SHOWING_HELP, feedbackArea.getText());
            assertEquals(helpScrollPane.getVmin(), helpScrollPane.getVvalue());
            enter(input, "view help");
            VBox commands = (VBox) root.lookup("#commands");
            assertTrue(commands.getChildren().stream().anyMatch(node -> node instanceof Label label
                    && label.getText().equals("Student commands")));
            enter(input, "list");
            assertEquals("help", tabs.getSelectionModel().getSelectedItem().getId(),
                    "Contact commands keep the current screen");
        });
    }

    private void verifyFailure() throws Exception {
        runOnFx(() -> {
            model.setActiveGroup(TypicalGroups.NAME_T01);
            Student firstStudent = model.getActiveGroupStudentList().getFirst();
            model.removeStudent(TypicalGroups.NAME_T01, firstStudent.getStudentId());
            assertEquals("1", studentTable().getColumns().getFirst().getCellObservableValue(0).getValue());
            enter(input, "edit 1 p/invalid");
            assertEquals("edit 1 p/invalid", input.getText());
            assertTrue(input.getStyleClass().contains("error"));
            TextArea feedback = (TextArea) root.lookup("#resultDisplay");
            assertEquals(Phone.MESSAGE_CONSTRAINTS, feedback.getText());
        });
    }

    /**
     * Asserts that the Students screen is titled for {@code expectedGroup} and lists the active group's students.
     */
    private void assertStudentsShown(String expectedGroup) {
        String expectedTitle = expectedGroup.equals(StudentListPanel.NO_ACTIVE_GROUP)
                ? StudentListPanel.STUDENTS_TITLE : expectedGroup;
        assertEquals(expectedTitle, ((Label) root.lookup("#groupTitle")).getText());
        assertEquals(model.getActiveGroupStudentList(), studentTable().getItems());
    }

    /**
     * Asserts that the empty Students screen explains itself with {@code expectedTitle}.
     */
    private void assertEmptyTitle(String expectedTitle) {
        assertTrue(studentTable().getItems().isEmpty());
        assertEquals(expectedTitle, ((Label) root.lookup("#emptyTitle")).getText());
    }

    /**
     * Asserts that the group strip shows a chip for each of {@code expectedNames}, in order, and that only the
     * chip named {@code expectedActiveName} is highlighted. An empty {@code expectedActiveName} means none is.
     */
    private void assertGroupChips(List<String> expectedNames, String expectedActiveName) {
        List<Label> chips = root.lookupAll(".group-chip").stream().map(node -> (Label) node).toList();
        assertEquals(expectedNames, chips.stream().map(Label::getText).toList());
        for (Label chip : chips) {
            boolean isActive = chip.getPseudoClassStates().contains(PseudoClass.getPseudoClass("active"));
            assertEquals(chip.getText().equals(expectedActiveName), isActive, chip.getText());
        }
    }

    /**
     * Asserts that only the navigation button of the screen with {@code keyword} is highlighted.
     */
    private void assertNavigationHighlights(String keyword) {
        PseudoClass current = PseudoClass.getPseudoClass("current");
        for (WorkspaceView view : WorkspaceView.values()) {
            Button button = (Button) root.lookup("#nav-" + view.getKeyword());
            assertEquals(view.getKeyword().equals(keyword), button.getPseudoClassStates().contains(current),
                    view.getKeyword());
        }
    }

    private static boolean hasVisibleVerticalScrollBar(Parent parent) {
        return parent.lookupAll(".scroll-bar").stream().anyMatch(node -> node instanceof ScrollBar bar
                && bar.getOrientation() == Orientation.VERTICAL && bar.isVisible());
    }

    private TableView<?> studentTable() {
        return (TableView<?>) root.lookup("#studentTable");
    }

    private void initializeWorkspace() {
        model = new ModelManager(SampleDataUtil.getSampleAddressBook(), TypicalGroups.getTypicalTAssist(),
                new UserPrefs());
        data = temporaryFolder.resolve("tassist.json");
        StorageManager storage = new StorageManager(new JsonTAssistStorage(data),
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
        assertEquals(StudentListPanel.STUDENTS_TITLE, ((Label) root.lookup("#groupTitle")).getText());
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
