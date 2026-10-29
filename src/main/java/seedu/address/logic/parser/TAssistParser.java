package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
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
    /** Matches a Unicode space character, such as a non-breaking space pasted from a web page. */
    private static final Pattern UNICODE_SPACE = Pattern.compile("\\p{Zs}");
    private static final Logger logger = LogsCenter.getLogger(TAssistParser.class);
    private static final Set<String> BUILT_IN_COMMAND_WORDS = Set.of(ExitCommand.COMMAND_WORD,
            HelpCommand.COMMAND_WORD, ViewCommand.COMMAND_WORD, UndoCommand.COMMAND_WORD, RedoCommand.COMMAND_WORD);

    private final CommandRegistry commandRegistry;
    private final List<HelpEntry> helpEntries;

    /**
     * Creates a parser for the built-in commands with no feature commands registered.
     */
    public TAssistParser() {
        this(new CommandRegistry());
    }

    /**
     * Creates a parser with a snapshot of feature registrations alongside the built-in commands.
     *
     * @param commandRegistry The feature registrations assembled before application startup.
     * @throws IllegalArgumentException if a feature keyword conflicts with a built-in command.
     */
    public TAssistParser(CommandRegistry commandRegistry) {
        requireNonNull(commandRegistry);
        checkArgument(BUILT_IN_COMMAND_WORDS.stream().noneMatch(commandRegistry::hasFeature),
                "Feature keywords must not conflict with the help, view, undo, redo and exit commands.");
        this.commandRegistry = new CommandRegistry(commandRegistry);
        helpEntries = HelpCatalog.getEntries(commandRegistry.getHelpEntries());
    }

    /**
     * Returns the help entries of the built-in commands followed by the registered feature commands.
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
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(replaceUnicodeSpaces(userInput).trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        // Note to developers: Change LOG_LEVEL in LogsCenter to enable lower level (i.e., FINE, FINER and lower)
        // log messages such as the one below.
        // Lower level log messages are used sparingly to minimize noise in the code.
        logger.fine("Command word: " + commandWord + "; Arguments: " + arguments);

        return switch (commandWord.toLowerCase(Locale.ROOT)) {
            case ExitCommand.COMMAND_WORD -> new ExitCommandParser().parse(arguments);
            case HelpCommand.COMMAND_WORD -> new HelpCommandParser(helpEntries).parse(arguments);
            case ViewCommand.COMMAND_WORD -> new ViewCommandParser().parse(arguments);
            case UndoCommand.COMMAND_WORD -> withoutParameters(arguments, new UndoCommand());
            case RedoCommand.COMMAND_WORD -> withoutParameters(arguments, new RedoCommand());
            default -> commandRegistry.parseCommand(commandWord, arguments);
        };
    }

    /**
     * Returns {@code userInput} with every Unicode space character replaced by a normal space.
     * Pasted text often contains spaces such as U+00A0 that look like a normal space but would not separate words.
     */
    static String replaceUnicodeSpaces(String userInput) {
        String normalizedInput = UNICODE_SPACE.matcher(userInput).replaceAll(" ");
        if (!normalizedInput.equals(userInput)) {
            logger.fine("Replaced Unicode space characters in the input with normal spaces.");
        }
        return normalizedInput;
    }

    /**
     * Returns {@code command} if {@code arguments} is blank.
     *
     * @throws ParseException if there is any input after the command word.
     */
    private static Command withoutParameters(String arguments, Command command) throws ParseException {
        if (!arguments.isBlank()) {
            throw new ParseException(TAssistArgumentParser.MESSAGE_NO_PARAMETERS);
        }
        return command;
    }

}
