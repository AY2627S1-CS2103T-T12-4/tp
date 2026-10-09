package seedu.address.logic;

import java.util.List;
import java.util.Optional;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;

/**
 * API of the Logic component
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Returns the in-app help entries available to the user.
     *
     * @return An immutable list of command help entries.
     */
    List<HelpEntry> getHelpEntries();

    /** Returns an unmodifiable view of the tutorial groups, in the order they were added. */
    ObservableList<Group> getGroupList();

    /**
     * Returns the active tutorial group, which is empty when no group is active.
     * It changes whenever another group becomes active, but not when the new active group is equal to the old one,
     * for example after the same group being reloaded. Read the students from
     * {@code getActiveGroupStudentList()} rather than from a {@code Group} taken from this value earlier.
     */
    ReadOnlyObjectProperty<Optional<Group>> activeGroupProperty();

    /**
     * Returns an unmodifiable view of the students in the active tutorial group.
     * It follows the active group, and is empty when no group is active.
     */
    ObservableList<Student> getActiveGroupStudentList();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);
}
