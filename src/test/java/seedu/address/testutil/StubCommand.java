package seedu.address.testutil;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * A {@code Command} for tests that runs a given action on the model and reports {@link #MESSAGE_SUCCESS}.
 */
public class StubCommand extends Command {
    public static final String MESSAGE_SUCCESS = "Stub command executed.";

    private final ModelAction action;

    /**
     * Creates a command that leaves the model unchanged.
     */
    public StubCommand() {
        this(model -> { });
    }

    /**
     * Creates a command that runs {@code action} on the model when executed.
     */
    public StubCommand(ModelAction action) {
        this.action = requireNonNull(action);
    }

    /**
     * Creates a command that fails with a {@code CommandException} carrying {@code message}.
     */
    public static StubCommand failingWith(String message) {
        return new StubCommand(model -> {
            throw new CommandException(message);
        });
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        action.run(model);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    /**
     * An action that a {@code StubCommand} runs on the model.
     */
    @FunctionalInterface
    public interface ModelAction {
        void run(Model model) throws CommandException;
    }
}
