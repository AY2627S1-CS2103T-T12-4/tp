package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import seedu.address.commons.core.WorkspaceView;
import seedu.address.model.person.Person;

/**
 * Hosts the seven TAssist screens while preserving the live roster across navigation.
 */
public class WorkspacePanel extends UiPart<TabPane> {
    private final Map<WorkspaceView, Tab> views = new EnumMap<>(WorkspaceView.class);
    private final PersonListPanel personListPanel;

    @FXML
    private TabPane tabs;

    /**
     * Creates the workspace with live contacts and explicit previews of future features.
     */
    public WorkspacePanel(ObservableList<Person> persons, Path dataFilePath) {
        super("WorkspacePanel.fxml");
        personListPanel = new PersonListPanel(persons);
        addView(WorkspaceView.STUDENTS, personListPanel.getRoot());
        addView(WorkspaceView.GROUPS, new FeaturePreview("Tutorial groups",
                "Create and switch between tutorial groups in a future increment.", "#", "GROUP", "STATUS").getRoot());
        addWeeklyView(WorkspaceView.ATTENDANCE, "Weekly attendance",
                "Present and absent records will appear here once attendance is implemented.", "ATTENDANCE");
        addWeeklyView(WorkspaceView.PARTICIPATION, "Weekly participation",
                "Weekly participation scores will appear here once participation is implemented.", "SCORE");
        addView(WorkspaceView.ASSIGNMENTS, new FeaturePreview("Assignment records",
                "Assignment selection, submissions and grades are coming soon.",
                "NAME", "STUDENT ID", "SUBMISSION", "GRADE").getRoot());
        addView(WorkspaceView.HELP, new HelpPanel().getRoot());
        addView(WorkspaceView.STORAGE, new StoragePanel(dataFilePath).getRoot());
        assert views.size() == WorkspaceView.values().length : "Every workspace screen must be registered";
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
     * Selects a screen without changing the current roster filter.
     */
    public void showView(WorkspaceView view) {
        tabs.getSelectionModel().select(views.get(requireNonNull(view)));
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }
}
