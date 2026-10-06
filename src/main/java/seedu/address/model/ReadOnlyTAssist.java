package seedu.address.model;

import java.util.Optional;

import javafx.collections.ObservableList;
import seedu.address.model.group.Group;

/**
 * Unmodifiable view of the TAssist data.
 */
public interface ReadOnlyTAssist {

    /**
     * Returns an unmodifiable view of the tutorial groups.
     * This list will not contain two groups with the same name.
     */
    ObservableList<Group> getGroupList();

    /**
     * Returns the active tutorial group, or an empty {@code Optional} if no group is active.
     */
    Optional<Group> getActiveGroup();

}
