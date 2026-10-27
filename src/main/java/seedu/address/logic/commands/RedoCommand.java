package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;

/**
 * Reapplies the latest change reversed by {@link UndoCommand}.
 */
public class RedoCommand extends Command {
    public static final String COMMAND_WORD = "redo";
    public static final String MESSAGE_SUCCESS = "Redone: %s";
    public static final String MESSAGE_NOTHING_TO_REDO =
            "Nothing to redo. Redo is available after undo, until you make another change.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (!model.canRedoTAssist()) {
            return new CommandResult(MESSAGE_NOTHING_TO_REDO);
        }

        String description = model.redoTAssist();
        return new CommandResult(String.format(MESSAGE_SUCCESS, description));
    }

    @Override
    public boolean isUndoable() {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof RedoCommand;
    }

    @Override
    public int hashCode() {
        return RedoCommand.class.hashCode();
    }
}
