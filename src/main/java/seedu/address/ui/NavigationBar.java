package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.commons.core.WorkspaceView;

/**
 * Lets the user open a workspace screen with the mouse.
 * The screens that hold the group's data come first, and the other screens sit at the right.
 * Every button opens the same screen as the command {@code view SCREEN}, and its tooltip names that command.
 */
public class NavigationBar extends UiPart<HBox> {
    private static final String FXML = "NavigationBar.fxml";
    private static final List<WorkspaceView> MAIN_VIEWS = List.of(WorkspaceView.STUDENTS, WorkspaceView.ATTENDANCE,
            WorkspaceView.PARTICIPATION, WorkspaceView.ASSIGNMENTS);
    private static final List<WorkspaceView> SIDE_VIEWS = List.of(WorkspaceView.GROUPS, WorkspaceView.HELP,
            WorkspaceView.STORAGE);
    /** Screens that only show a layout, until their features exist. */
    private static final Set<WorkspaceView> PLANNED_VIEWS = EnumSet.of(WorkspaceView.GROUPS,
            WorkspaceView.ATTENDANCE, WorkspaceView.PARTICIPATION, WorkspaceView.ASSIGNMENTS);
    private static final PseudoClass CURRENT = PseudoClass.getPseudoClass("current");

    private final Map<WorkspaceView, Button> buttons = new EnumMap<>(WorkspaceView.class);
    private final Consumer<WorkspaceView> viewOpener;

    @FXML
    private HBox mainButtons;
    @FXML
    private HBox sideButtons;

    /**
     * Creates a bar whose buttons call {@code viewOpener} with the screen to open.
     */
    public NavigationBar(Consumer<WorkspaceView> viewOpener) {
        super(FXML);
        this.viewOpener = requireNonNull(viewOpener);
        MAIN_VIEWS.forEach(view -> mainButtons.getChildren().add(createButton(view)));
        SIDE_VIEWS.forEach(view -> sideButtons.getChildren().add(createButton(view)));
        assert buttons.size() == WorkspaceView.values().length : "Every workspace screen needs a button";
    }

    /**
     * Highlights the button of {@code view}, which is the screen now shown.
     */
    public void showCurrent(WorkspaceView view) {
        requireNonNull(view);
        buttons.forEach((buttonView, button) -> button.pseudoClassStateChanged(CURRENT, buttonView == view));
    }

    private Button createButton(WorkspaceView view) {
        Button button = new Button(view.getTitle());
        button.setId("nav-" + view.getKeyword());
        button.getStyleClass().add("nav-button");
        // Keep keyboard focus in the command box when a button is clicked.
        button.setFocusTraversable(false);
        button.setOnAction(event -> viewOpener.accept(view));
        button.setTooltip(new Tooltip(describe(view)));
        if (PLANNED_VIEWS.contains(view)) {
            Region soonDot = new Region();
            soonDot.getStyleClass().add("soon-dot");
            button.setGraphic(soonDot);
            button.setContentDisplay(ContentDisplay.RIGHT);
            button.setGraphicTextGap(6);
        }
        buttons.put(view, button);
        return button;
    }

    /**
     * Returns the tooltip text, which names the command that does the same as the button.
     */
    static String describe(WorkspaceView view) {
        String command = "Same as typing: view " + view.getKeyword();
        return PLANNED_VIEWS.contains(view) ? "Coming soon. " + command : command;
    }
}
