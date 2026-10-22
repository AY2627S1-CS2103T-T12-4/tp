package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
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
import seedu.address.commons.core.DataLoadStatus;
import seedu.address.commons.core.WorkspaceView;
import seedu.address.logic.LogicManager;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.parser.AttendanceCommandRegistration;
import seedu.address.logic.parser.CommandRegistry;
import seedu.address.logic.parser.GroupCommandRegistration;
import seedu.address.logic.parser.ParticipationCommandRegistration;
import seedu.address.logic.parser.StudentCommandRegistration;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyTAssist;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.Student;
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
    public void startup_missingOrUnreadableData_showsLoadOutcome() throws Exception {
        for (DataLoadStatus status : List.of(DataLoadStatus.MISSING, DataLoadStatus.FAILED)) {
            runOnFx(() -> initializeWorkspace(status));
            try {
                runOnFx(() -> {
                    assertEquals(status.getMessage(), ((TextArea) root.lookup("#resultDisplay")).getText());
                    assertTrue(((Label) root.lookup("#saveLocationStatus")).getText()
                            .startsWith(status.getMessage()));
                    assertEquals(status.isFailure(), isFooterShowingFailure());
                });
            } finally {
                runOnFx(() -> stage.hide());
            }
        }
    }

    @Test
    public void saveFailure_showsErrorWithoutSuccessAndRestoresWorkspace() throws Exception {
        for (IOException failure : List.of(new IOException("Disk full"), new AccessDeniedException("Data folder"))) {
            runOnFx(() -> initializeWorkspace(DataLoadStatus.LOADED, failure));
            try {
                runOnFx(() -> {
                    TAssist previousData = new TAssist(model.getTAssist());
                    enter(input, "group add g/T04");
                    String expected = failure instanceof AccessDeniedException
                            ? LogicManager.MESSAGE_SAVE_PERMISSION_FAILURE : LogicManager.MESSAGE_SAVE_FAILURE;
                    TextArea feedback = (TextArea) root.lookup("#resultDisplay");
                    assertEquals(expected, feedback.getText());
                    assertEquals("group add g/T04", input.getText());
                    assertTrue(isFooterShowingFailure());
                    assertFalse(((Label) root.lookup("#saveLocationStatus")).getText().contains("Changes saved."));
                    assertEquals(previousData, model.getTAssist());
                    assertGroupChips(List.of("T01", "T02", "T03"), "");
                    assertStudentsShown(StudentListPanel.NO_ACTIVE_GROUP);
                    enter(input, "help");
                    assertEquals(HelpCommand.MESSAGE_SHOWING_HELP, feedback.getText());
                    assertFalse(isFooterShowingFailure());
                });
                assertFalse(Files.exists(data));
            } finally {
                runOnFx(() -> stage.hide());
            }
        }
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
            runOnFx(() -> enter(input, "view"));
            runOnFx(() -> {
                render(root, "error-usage", 1280, 680);
                TextArea feedback = (TextArea) root.lookup("#resultDisplay");
                assertTrue(feedback.getText().contains(ViewCommand.MESSAGE_USAGE));
                assertFalse(hasVisibleVerticalScrollBar(feedback), "A usage message fits without scrolling");
            });
            runOnFx(() -> {
                enter(input, "view storage");
                Label status = (Label) root.lookup("#saveLocationStatus");
                assertTrue(status.getText().startsWith("Local data file"));
                assertFalse(isFooterShowingFailure(), "A later successful command clears the failure mark");
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
            runOnFx(() -> render(root, "no-active-group-minimum", 640, 420));

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
    public void groupCommands_updateChipsStudentsAndFeedback() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                enter(input, "group use g/t01");
                assertStudentsShown("T01");
                assertGroupChips(List.of("T01", "T02", "T03"), "T01");
                assertEquals(TypicalGroups.getT01().getStudentList(), studentTable().getItems());
                enter(input, "group use g/T02");
                assertStudentsShown("T02");
                assertEquals(TypicalGroups.getT02().getStudentList(), studentTable().getItems());
                enter(input, "group add g/ Thursday   1 ");
                Label status = (Label) root.lookup("#saveLocationStatus");
                assertTrue(status.getText().startsWith("Changes saved."));
                assertStudentsShown("Thursday 1");
                assertTrue(studentTable().getItems().isEmpty());
                assertGroupChips(List.of("T01", "T02", "T03", "Thursday 1"), "Thursday 1");
                TextArea feedback = (TextArea) root.lookup("#resultDisplay");
                assertEquals("Created tutorial group: Thursday 1.\nActive group is now Thursday 1.",
                        feedback.getText());
                enter(input, "group list");
                assertTrue(status.getText().startsWith("Local data file"));
                assertEquals("1. T01\n2. T02\n3. T03\n4. Thursday 1\nActive group: Thursday 1", feedback.getText());
            });
            runOnFx(() -> render(root, "group-commands", 853, 440));
            runOnFx(() -> {
                enter(input, "group add g/t01");
                assertEquals("group add g/t01", input.getText());
                assertTrue(isFooterShowingFailure());
                assertStudentsShown("Thursday 1");
                enter(input, "group use g/T01");
                assertEquals(TypicalGroups.getT01().getStudentList(), studentTable().getItems());
                enter(input, "help group");
                assertEquals("help", tabs.getSelectionModel().getSelectedItem().getId());
                assertEquals("Showing group commands.", ((TextArea) root.lookup("#resultDisplay")).getText());
            });
            runOnFx(() -> render(root, "group-help", 1280, 680));
        } finally {
            runOnFx(() -> stage.hide());
        }
    }

    @Test
    public void participationCommands_showScoresAndKeepRejectedInput() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                enter(input, "group use g/T01");
                enter(input, "view participation");
                TextArea feedback = (TextArea) root.lookup("#resultDisplay");
                enter(input, "participation set id/a0123456x w/3 s/0");
                assertEquals("Recorded participation score 0 for Alice Tan in Week 3.", feedback.getText());
                enter(input, "participation list w/3");
                assertEquals("Participation for T01, Week 3:\nAlice Tan | A0123456X | 0"
                        + "\nBen Lim | A0234567Y | Not recorded\nCarl Goh | A0345678Z | Not recorded",
                        feedback.getText());
                enter(input, "participation set id/A0123456X w/3 s/6");
                assertEquals("Invalid participation score. Enter a whole number from 0 to 5.", feedback.getText());
                assertEquals("participation set id/A0123456X w/3 s/6", input.getText());
                assertTrue(input.getStyleClass().contains("error"));
                enter(input, "participation set id/A0123456X w/3 s/4");
                assertEquals("Updated Alice Tan's participation score for Week 3 to 4.", feedback.getText());
                assertTrue(input.getText().isEmpty());
                assertEquals("participation", tabs.getSelectionModel().getSelectedItem().getId());
            });
            runOnFx(() -> render(root, "participation-commands", 853, 440));
        } finally {
            runOnFx(() -> stage.hide());
        }
    }

    @Test
    public void studentCommands_updateRosterAndKeepFailedInput() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                enter(input, "group use g/T03");
                enter(input, "student add n/Alice Tan id/A1");
                enter(input, "student add n/Ben Lim id/A2");
                assertEquals(2, studentTable().getItems().size());
                TextArea feedback = (TextArea) root.lookup("#resultDisplay");
                enter(input, "student list");
                assertEquals("Students in T03:\n1. Alice Tan | A1\n2. Ben Lim | A2", feedback.getText());
                enter(input, "student add n/Alice Tan id/a1");
                assertEquals("Student ID A1 already exists in T03.", feedback.getText());
                assertEquals("student add n/Alice Tan id/a1", input.getText());
                assertEquals(2, studentTable().getItems().size());
                enter(input, "student delete id/a1");
                assertEquals("Removed Alice Tan from T03.", feedback.getText());
                assertEquals(1, studentTable().getItems().size());
                assertEquals("1", studentTable().getColumns().getFirst().getCellObservableValue(0).getValue());
                enter(input, "student delete id/A2");
                assertEmptyTitle(String.format(StudentListPanel.EMPTY_GROUP_TITLE_FORMAT, "T03"));
            });
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

    @Test
    public void attendanceCommands_largeGroup_showsScrollableFeedbackAndRetainsInvalidInput() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                model.setActiveGroup(TypicalGroups.NAME_T01);
                for (int i = 0; i < 17; i++) {
                    model.addStudent(TypicalGroups.NAME_T01, new StudentBuilder().withName("Student")
                            .withStudentId("A" + (100 + i)).build());
                }
                enter(input, "attendance mark id/A0123456X w/3 s/present");
                TextArea feedback = (TextArea) root.lookup("#resultDisplay");
                assertEquals("Recorded Alice Tan as present for Week 3.", feedback.getText());
                enter(input, "attendance list w/3");
                assertEquals(21, feedback.getText().lines().count());
                assertTrue(feedback.getText().contains("Alice Tan | A0123456X | Present"));
                assertTrue(feedback.getText().contains("Student | A116 | Not recorded"));
                assertEquals("students", tabs.getSelectionModel().getSelectedItem().getId());
                assertTrue(input.getText().isEmpty());
            });
            runOnFx(() -> render(root, "attendance-list", 853, 440));
            runOnFx(() -> {
                TextArea feedback = (TextArea) root.lookup("#resultDisplay");
                assertTrue(hasVisibleVerticalScrollBar(feedback), "A full tutorial group's attendance can scroll");
                enter(input, "attendance mark id/A0123456X w/3 s/late");
                assertEquals("attendance mark id/A0123456X w/3 s/late", input.getText());
                assertTrue(input.getStyleClass().contains("error"));
                assertEquals("Invalid attendance status. Use present or absent.", feedback.getText());
            });
        } finally {
            runOnFx(() -> stage.hide());
        }
    }

    @Test
    public void commandBox_upAndDownKeys_recallEarlierCommandsForEditing() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                enter(input, "view help");
                enter(input, "view nowhere");
                input.setText("view stor");
                pressKey(KeyCode.UP);
                assertEquals("view nowhere", input.getText());
                assertEquals("view nowhere".length(), input.getCaretPosition());
                pressKey(KeyCode.UP);
                pressKey(KeyCode.UP);
                assertEquals("view help", input.getText(), "Up stops at the oldest command");
                pressKey(KeyCode.DOWN);
                assertEquals("view nowhere", input.getText(), "Failed commands are recalled too");
                pressKey(KeyCode.DOWN);
                pressKey(KeyCode.DOWN);
                assertEquals("view stor", input.getText(), "Down past the newest command restores the typed text");

                pressKey(KeyCode.UP);
                input.setText(input.getText().replace("nowhere", "storage"));
                input.fireEvent(new ActionEvent());
                assertEquals("storage", tabs.getSelectionModel().getSelectedItem().getId());
                assertTrue(input.getText().isEmpty());
                pressKey(KeyCode.UP);
                assertEquals("view storage", input.getText(), "The edited command is the newest entry");
                input.setText("view st");
                pressShiftKey(KeyCode.UP);
                assertEquals("view st", input.getText(), "Shift+Up is left to the text field");
            });
        } finally {
            runOnFx(() -> stage.hide());
        }
    }

    @Test
    public void commandBox_recalledCommandFails_keepsItEditableAndHistoryUsable() throws Exception {
        runOnFx(this::initializeWorkspace);
        try {
            runOnFx(() -> {
                enter(input, "view help");
                enter(input, "view storage");
                pressKey(KeyCode.UP);
                input.setText(input.getText().replace("storage", "storge"));
                input.fireEvent(new ActionEvent());
                assertEquals("view storge", input.getText());
                assertTrue(input.getStyleClass().contains("error"));

                pressKey(KeyCode.UP);
                assertEquals("view storage", input.getText(), "Up skips the failed command already in the box");
                pressKey(KeyCode.DOWN);
                assertEquals("view storge", input.getText());
                input.setText(input.getText().replace("storge", "storage"));
                input.fireEvent(new ActionEvent());
                assertEquals("storage", tabs.getSelectionModel().getSelectedItem().getId());
                assertTrue(input.getText().isEmpty());
                assertFalse(input.getStyleClass().contains("error"));
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
            input.setText("view students");
            pressKey(KeyCode.F1);
            assertEquals("help", tabs.getSelectionModel().getSelectedItem().getId());
            assertEquals("view students", input.getText());
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
            enter(input, "student list");
            assertEquals("help", tabs.getSelectionModel().getSelectedItem().getId(),
                    "Commands that do not open a screen keep the current screen");
        });
    }

    private void verifyFailure() throws Exception {
        runOnFx(() -> {
            model.setActiveGroup(TypicalGroups.NAME_T01);
            Student firstStudent = model.getActiveGroupStudentList().getFirst();
            model.removeStudent(TypicalGroups.NAME_T01, firstStudent.getStudentId());
            assertEquals("1", studentTable().getColumns().getFirst().getCellObservableValue(0).getValue());
            enter(input, "view nowhere");
            assertEquals("view nowhere", input.getText());
            assertTrue(input.getStyleClass().contains("error"));
            TextArea feedback = (TextArea) root.lookup("#resultDisplay");
            assertEquals(String.format(ViewCommand.MESSAGE_UNKNOWN_SCREEN, "nowhere"), feedback.getText());
            assertTrue(isFooterShowingFailure(), "The footer marks a failed command");
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

    private boolean isFooterShowingFailure() {
        return root.lookup(".status-bar").getPseudoClassStates().contains(PseudoClass.getPseudoClass("failure"));
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
        initializeWorkspace(DataLoadStatus.LOADED);
    }

    private void initializeWorkspace(DataLoadStatus dataLoadStatus) {
        initializeWorkspace(dataLoadStatus, null);
    }

    private void initializeWorkspace(DataLoadStatus dataLoadStatus, IOException saveFailure) {
        model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        data = temporaryFolder.resolve("tassist.json");
        JsonTAssistStorage dataStorage = new JsonTAssistStorage(data) {
            @Override
            public void saveTAssist(ReadOnlyTAssist tAssist) throws IOException {
                if (saveFailure != null) {
                    throw saveFailure;
                }
                super.saveTAssist(tAssist);
            }
        };
        StorageManager storage = new StorageManager(dataStorage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("prefs.json")));
        CommandRegistry commandRegistry = new CommandRegistry();
        GroupCommandRegistration.registerCommands(commandRegistry);
        AttendanceCommandRegistration.registerCommands(commandRegistry);
        ParticipationCommandRegistration.registerCommands(commandRegistry);
        StudentCommandRegistration.registerCommands(commandRegistry);
        stage = new Stage();
        MainWindow window = new MainWindow(stage,
                new LogicManager(model, storage, commandRegistry, dataLoadStatus), data, dataLoadStatus);
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

    private void pressKey(KeyCode key) {
        input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key, false, false, false, false));
    }

    private void pressShiftKey(KeyCode key) {
        input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key, true, false, false, false));
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
