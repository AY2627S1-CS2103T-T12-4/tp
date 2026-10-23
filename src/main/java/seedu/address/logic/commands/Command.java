package seedu.address.logic.commands;

import java.util.Optional;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * Represents a command with hidden internal logic and the ability to be executed.
 */
public abstract class Command {

    /**
     * Returns a confirmation prompt when this command may remove user data.
     *
     * @param model {@code Model} which the command should operate on.
     * @return A prompt to show before execution, or empty if confirmation is unnecessary.
     */
    public Optional<String> getConfirmationPrompt(Model model) {
        return Optional.empty();
    }

    /**
     * Executes the command and returns the result message.
     *
     * @param model {@code Model} which the command should operate on.
     * @return feedback message of the operation result for display
     * @throws CommandException If an error occurs during command execution.
     */
    public abstract CommandResult execute(Model model) throws CommandException;

}
