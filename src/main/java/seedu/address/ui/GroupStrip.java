package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import seedu.address.model.group.Group;

/**
 * Shows every tutorial group as a chip in the top bar, with the active group highlighted.
 */
public class GroupStrip extends UiPart<ScrollPane> {
    static final String NO_GROUPS = "No tutorial groups yet";
    static final String DATA_NOT_READ = "Saved data could not be read";

    private static final String FXML = "GroupStrip.fxml";
    private static final PseudoClass ACTIVE = PseudoClass.getPseudoClass("active");
    private static final double CHIP_MAX_WIDTH = 200;

    private final ObservableList<Group> groups;
    private final ObservableValue<Optional<Group>> activeGroup;
    private final boolean isDataLoadFailed;

    @FXML
    private HBox groupChips;

    /**
     * Creates a strip that follows {@code groups} and highlights {@code activeGroup}. If {@code isDataLoadFailed},
     * an empty strip says the saved data could not be read instead of suggesting there are no groups yet.
     */
    public GroupStrip(ObservableList<Group> groups, ObservableValue<Optional<Group>> activeGroup,
            boolean isDataLoadFailed) {
        super(FXML);
        requireAllNonNull(groups, activeGroup);
        this.groups = groups;
        this.activeGroup = activeGroup;
        this.isDataLoadFailed = isDataLoadFailed;

        groups.addListener((ListChangeListener<Group>) change -> showGroups());
        activeGroup.addListener(observable -> showGroups());
        showGroups();
    }

    /**
     * Replaces the chips with one chip per group, or a note when there are no groups.
     */
    private void showGroups() {
        groupChips.getChildren().clear();
        if (groups.isEmpty()) {
            Label noGroups = new Label(isDataLoadFailed ? DATA_NOT_READ : NO_GROUPS);
            noGroups.getStyleClass().add("muted");
            groupChips.getChildren().add(noGroups);
            return;
        }
        for (Group group : groups) {
            groupChips.getChildren().add(createChip(group));
        }
    }

    private Label createChip(Group group) {
        String name = group.getName().toString();
        Label chip = new Label(name);
        chip.getStyleClass().add("group-chip");
        chip.setMaxWidth(CHIP_MAX_WIDTH);
        chip.setTooltip(new Tooltip(name));
        chip.pseudoClassStateChanged(ACTIVE, isActive(group));
        return chip;
    }

    private boolean isActive(Group group) {
        return activeGroup.getValue().map(group::isSameGroup).orElse(false);
    }
}
