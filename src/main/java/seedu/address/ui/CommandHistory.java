package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stores the commands entered in this session so that they can be recalled with the Up and Down keys.
 * The history is kept in memory only and starts empty each time the app opens.
 */
public class CommandHistory {

    private final List<String> commands = new ArrayList<>();

    /**
     * Text the TA left at each position while moving through the history. The position after the newest command
     * holds the command the TA was typing before recalling an earlier one.
     */
    private final Map<Integer, String> editedTexts = new HashMap<>();

    /** Position of the recalled command, or the number of commands when the TA is typing a new one. */
    private int position;

    /**
     * Adds {@code command} as the newest entry and stops recalling, so the next Up recalls this command.
     * Edits to recalled commands are discarded, and the stored commands stay as they were entered.
     */
    public void add(String command) {
        requireNonNull(command);
        commands.add(command);
        position = commands.size();
        editedTexts.clear();
    }

    /**
     * Returns the command entered before the one shown, or an empty {@code Optional} if none is earlier.
     * {@code currentText} is kept at the current position so that moving back to it restores any edits.
     */
    public Optional<String> previous(String currentText) {
        requireNonNull(currentText);
        if (position == 0) {
            return Optional.empty();
        }

        return Optional.of(moveTo(position - 1, currentText));
    }

    /**
     * Returns the command entered after the one shown, the text the TA was typing once past the newest command,
     * or an empty {@code Optional} if the TA is not recalling a command.
     * {@code currentText} is kept at the current position so that moving back to it restores any edits.
     */
    public Optional<String> next(String currentText) {
        requireNonNull(currentText);
        if (isTypingNewCommand()) {
            return Optional.empty();
        }

        return Optional.of(moveTo(position + 1, currentText));
    }

    private String moveTo(int newPosition, String currentText) {
        assert newPosition >= 0 && newPosition <= commands.size() : "Position must stay within the history";
        editedTexts.put(position, currentText);
        position = newPosition;
        return getTextAt(position);
    }

    private String getTextAt(int index) {
        if (editedTexts.containsKey(index)) {
            return editedTexts.get(index);
        }

        assert index < commands.size() : "Typed text is saved before an earlier command is recalled";
        return commands.get(index);
    }

    private boolean isTypingNewCommand() {
        return position == commands.size();
    }
}
