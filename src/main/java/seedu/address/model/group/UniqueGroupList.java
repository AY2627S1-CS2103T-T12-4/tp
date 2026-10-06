package seedu.address.model.group;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Optional;

import seedu.address.model.group.exceptions.DuplicateGroupException;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.model.util.UniqueList;

/**
 * A list of tutorial groups that enforces unique group names, compared case-insensitively.
 *
 * @see Group#isSameGroup(Group)
 */
public class UniqueGroupList extends UniqueList<Group> {

    @Override
    protected boolean isSameItem(Group first, Group second) {
        return first.isSameGroup(second);
    }

    @Override
    protected RuntimeException createDuplicateException() {
        return new DuplicateGroupException();
    }

    @Override
    protected RuntimeException createNotFoundException() {
        return new GroupNotFoundException();
    }

    /**
     * Returns true if the list contains a group named {@code name}.
     */
    public boolean contains(GroupName name) {
        return find(name).isPresent();
    }

    /**
     * Returns the group named {@code name}, or an empty {@code Optional} if there is no such group.
     */
    public Optional<Group> find(GroupName name) {
        requireNonNull(name);
        return findFirst(group -> group.getName().equals(name));
    }

    /**
     * Replaces the contents of this list with {@code groups}.
     * {@code groups} must not contain groups with the same name.
     */
    public void setGroups(List<Group> groups) {
        setAll(groups);
    }
}
