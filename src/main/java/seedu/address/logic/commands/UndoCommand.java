package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.TAssistHistory;

/**
 * Reverses the latest change to TAssist data, including which group is active.
 */
public class UndoCommand extends Command {
    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Undone: %s";
    public static final String MESSAGE_NOTHING_TO_UNDO = "Nothing to undo. TAssist can undo the last "
            + TAssistHistory.MAX_CHANGES + " changes made since it was opened.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (!model.canUndoTAssist()) {
            return new CommandResult(MESSAGE_NOTHING_TO_UNDO);
        }

        String description = model.undoTAssist();
        return new CommandResult(String.format(MESSAGE_SUCCESS, description));
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof UndoCommand;
    }

    @Override
    public int hashCode() {
        return UndoCommand.class.hashCode();
    }
}
