package seedu.address.logic.help;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.List;

/**
 * Stores the usage information for one command in the in-app reference.
 */
public final class HelpEntry {
    private final String topic;
    private final String commandFormat;
    private final String description;
    private final List<String> examples;

    /**
     * Creates a help entry for a command.
     *
     * @param topic The owning feature keyword, or an empty string for existing general commands.
     * @param commandFormat The command format shown to the user.
     * @param description A brief explanation of the command.
     * @param examples One or more example commands.
     */
    public HelpEntry(String topic, String commandFormat, String description, List<String> examples) {
        this.topic = requireNonNull(topic);
        this.commandFormat = requireNonNull(commandFormat);
        this.description = requireNonNull(description);
        this.examples = List.copyOf(requireNonNull(examples));

        checkArgument(!commandFormat.isBlank(), "Command format must not be blank.");
        checkArgument(!description.isBlank(), "Description must not be blank.");
        checkArgument(!this.examples.isEmpty() && this.examples.stream().noneMatch(String::isBlank),
                "At least one non-blank example is required.");
    }

    public String getTopic() {
        return topic;
    }

    public String getCommandFormat() {
        return commandFormat;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getExamples() {
        return examples;
    }
}
