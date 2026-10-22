package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stores the commands entered in this session so that they can be recalled with the Up and Down keys.
 * The history is kept in memory only and starts empty each time the app opens.
 */
public class CommandHistory {

    private final List<String> commands = new ArrayList<>();

    /** Position of the recalled command, or the number of commands when the TA is typing a new one. */
    private int position;

    /** Text the TA was typing before recalling an earlier command. */
    private String draft = "";

    /**
     * Adds {@code command} as the newest entry and stops recalling, so the next Up recalls this command.
     */
    public void add(String command) {
        requireNonNull(command);
        commands.add(command);
        position = commands.size();
        draft = "";
    }

    /**
     * Returns the command entered before the one shown, or an empty {@code Optional} if none is earlier.
     * When the TA starts recalling, {@code currentText} is kept so that {@link #next()} can restore it.
     */
    public Optional<String> previous(String currentText) {
        requireNonNull(currentText);
        if (position == 0) {
            return Optional.empty();
        }

        if (isTypingNewCommand()) {
            draft = currentText;
        }
        position--;
        return Optional.of(commands.get(position));
    }

    /**
     * Returns the command entered after the one shown, the text the TA was typing once past the newest command,
     * or an empty {@code Optional} if the TA is not recalling a command.
     */
    public Optional<String> next() {
        if (isTypingNewCommand()) {
            return Optional.empty();
        }

        position++;
        assert position <= commands.size() : "Position must not pass the text being typed";
        return Optional.of(isTypingNewCommand() ? draft : commands.get(position));
    }

    private boolean isTypingNewCommand() {
        return position == commands.size();
    }
}
