package seedu.address.testutil;

import java.util.List;

import seedu.address.logic.help.HelpEntry;

/**
 * A utility class to help with building {@code HelpEntry} objects.
 */
public class HelpEntryBuilder {
    public static final String DEFAULT_TOPIC = "student";
    public static final String DEFAULT_COMMAND_FORMAT = "student list";
    public static final String DEFAULT_DESCRIPTION = "Description";

    private String topic = DEFAULT_TOPIC;
    private String commandFormat = DEFAULT_COMMAND_FORMAT;
    private String description = DEFAULT_DESCRIPTION;
    private List<String> examples = List.of(DEFAULT_COMMAND_FORMAT);

    /**
     * Sets the {@code topic} of the {@code HelpEntry} being built.
     */
    public HelpEntryBuilder withTopic(String topic) {
        this.topic = topic;
        return this;
    }

    /**
     * Sets the {@code commandFormat} of the {@code HelpEntry} being built and uses it as its only example.
     */
    public HelpEntryBuilder withCommandFormat(String commandFormat) {
        this.commandFormat = commandFormat;
        this.examples = List.of(commandFormat);
        return this;
    }

    /**
     * Sets the {@code description} of the {@code HelpEntry} being built.
     */
    public HelpEntryBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Sets the {@code examples} of the {@code HelpEntry} being built.
     */
    public HelpEntryBuilder withExamples(String... examples) {
        this.examples = List.of(examples);
        return this;
    }

    public HelpEntry build() {
        return new HelpEntry(topic, commandFormat, description, examples);
    }
}
