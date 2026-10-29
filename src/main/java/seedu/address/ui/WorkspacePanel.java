package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import seedu.address.commons.core.WorkspaceView;
import seedu.address.logic.help.HelpEntry;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;

/**
 * Hosts the seven TAssist screens while preserving the live student roster across navigation.
 */
public class WorkspacePanel extends UiPart<TabPane> {
    private final Map<WorkspaceView, Tab> views = new EnumMap<>(WorkspaceView.class);
    private final HelpPanel helpPanel;
    private final NavigationBar navigationBar = new NavigationBar(this::showView);

    @FXML
    private TabPane tabs;

    /**
     * Creates the workspace with the active group's live students and explicit previews of future features.
     */
    public WorkspacePanel(ObservableValue<Optional<Group>> activeGroup, ObservableList<Student> students,
            ObservableList<Student> displayedStudents, ObservableBooleanValue hasStudentFilter,
            Path dataFilePath, List<HelpEntry> helpEntries, Consumer<Student> studentClickHandler) {
        super("WorkspacePanel.fxml");
        requireNonNull(helpEntries);
        StudentListPanel studentListPanel = new StudentListPanel(activeGroup, students, displayedStudents,
                hasStudentFilter, studentClickHandler);
        addView(WorkspaceView.STUDENTS, studentListPanel.getRoot());
        addView(WorkspaceView.GROUPS, new FeaturePreview("Tutorial groups",
                "Use group add, group list, and group use to manage tutorial groups. This table is coming soon.",
                "#", "GROUP", "STATUS").getRoot());
        addWeeklyView(WorkspaceView.ATTENDANCE, "Weekly attendance",
                "Use attendance mark and attendance list for records in the feedback box. This table is coming soon.",
                "ATTENDANCE");
        addWeeklyView(WorkspaceView.PARTICIPATION, "Weekly participation",
                "Use participation set and participation list to manage scores. This table is coming soon.", "SCORE");
        addView(WorkspaceView.ASSIGNMENTS, new FeaturePreview("Assignment records",
                "Use assignment add, assignment submit, assignment grade, and assignment list to manage records.",
                "NAME", "STUDENT ID", "SUBMISSION", "GRADE").getRoot());
        helpPanel = new HelpPanel(helpEntries);
        addView(WorkspaceView.HELP, helpPanel.getRoot());
        addView(WorkspaceView.STORAGE, new StoragePanel(dataFilePath).getRoot());
        assert views.size() == WorkspaceView.values().length : "Every workspace screen must be registered";
        tabs.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) ->
                navigationBar.showCurrent(findView(newTab)));
        navigationBar.showCurrent(findView(tabs.getSelectionModel().getSelectedItem()));
    }

    /**
     * Returns the bar that opens each screen, for the window to place above the workspace.
     */
    public Node getNavigationBar() {
        return navigationBar.getRoot();
    }

    private WorkspaceView findView(Tab tab) {
        return views.entrySet().stream().filter(entry -> entry.getValue() == tab).map(Map.Entry::getKey)
                .findFirst().orElseThrow();
    }

    private void addWeeklyView(WorkspaceView view, String title, String explanation, String column) {
        FeaturePreview preview = new FeaturePreview(title, explanation, "NAME", "STUDENT ID", column);
        preview.showWeeks();
        addView(view, preview.getRoot());
    }

    private void addView(WorkspaceView view, Node content) {
        Tab tab = new Tab(view.getTitle(), content);
        tab.setId(view.getKeyword());
        views.put(view, tab);
        tabs.getTabs().add(tab);
    }

    /**
     * Selects a screen.
     */
    public void showView(WorkspaceView view) {
        tabs.getSelectionModel().select(views.get(requireNonNull(view)));
    }

    /**
     * Opens Help at the commands of {@code topic}, or at the top if {@code topic} is empty.
     */
    public void showHelp(String topic) {
        showView(WorkspaceView.HELP);
        // Lay out the newly selected Help screen first, so that it can find where the topic starts.
        getRoot().applyCss();
        getRoot().layout();
        helpPanel.scrollToTopic(topic);
    }
}
