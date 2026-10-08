package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Locale;

import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an optional topic for the help command.
 */
public class HelpCommandParser implements Parser<HelpCommand> {
    private final List<HelpEntry> helpEntries;

    /**
     * Creates a parser whose commands show entries from {@code helpEntries}.
     */
    public HelpCommandParser(List<HelpEntry> helpEntries) {
        this.helpEntries = List.copyOf(requireNonNull(helpEntries));
    }

    @Override
    public HelpCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String topic = args.trim();
        if (topic.isEmpty()) {
            return new HelpCommand(helpEntries);
        }
        if (!HelpCatalog.isValidTopic(topic)) {
            throw new ParseException(HelpCommand.MESSAGE_UNKNOWN_TOPIC);
        }
        return new HelpCommand(helpEntries, topic.toLowerCase(Locale.ROOT));
    }
}
