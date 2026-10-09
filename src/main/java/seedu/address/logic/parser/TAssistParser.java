package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ViewCommand;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input into TAssist commands.
 */
public class TAssistParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT =
            Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)", Pattern.DOTALL);
    private static final Logger logger = LogsCenter.getLogger(TAssistParser.class);
    private static final Set<String> LEGACY_COMMAND_WORDS = Set.of(ExitCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD, ViewCommand.COMMAND_WORD);

    private final CommandRegistry commandRegistry;
    private final List<HelpEntry> helpEntries;

    /**
     * Creates a parser for legacy commands with no feature commands registered.
     */
    public TAssistParser() {
        this(new CommandRegistry());
    }

    /**
     * Creates a parser with a snapshot of feature registrations alongside legacy commands.
     *
     * @param commandRegistry The feature registrations assembled before application startup.
     * @throws IllegalArgumentException if a feature keyword conflicts with a legacy command.
     */
    public TAssistParser(CommandRegistry commandRegistry) {
        requireNonNull(commandRegistry);
        checkArgument(LEGACY_COMMAND_WORDS.stream().noneMatch(commandRegistry::hasFeature),
                "Feature keywords must not conflict with the help, view and exit commands.");
        this.commandRegistry = new CommandRegistry(commandRegistry);
        helpEntries = HelpCatalog.getEntries(commandRegistry.getHelpEntries());
    }

    /**
     * Returns the help entries of legacy commands followed by the registered feature commands.
     *
     * @return An immutable list of command help entries.
     */
    public List<HelpEntry> getHelpEntries() {
        return helpEntries;
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform to the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        requireNonNull(userInput);
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change LOG_LEVEL in LogsCenter to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        return switch (commandWord) {
            case ExitCommand.COMMAND_WORD -> new ExitCommand();
            case HelpCommand.COMMAND_WORD -> new HelpCommandParser(helpEntries).parse(arguments);
            case ViewCommand.COMMAND_WORD -> new ViewCommandParser().parse(arguments);
            default -> commandRegistry.parseCommand(commandWord, arguments);
        };
    }

}
