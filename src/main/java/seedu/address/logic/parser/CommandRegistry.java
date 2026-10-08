package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.Command;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Registers feature-owned parsers for two-word commands.
 */
public class CommandRegistry {
    public static final String MESSAGE_MISSING_SUBCOMMAND =
            "Missing subcommand for '%s'. Valid subcommands: %s. Enter %s SUBCOMMAND.";
    public static final String MESSAGE_UNKNOWN_SUBCOMMAND =
            "Unknown subcommand '%s' for '%s'. Valid subcommands: %s.";

    private static final Logger logger = LogsCenter.getLogger(CommandRegistry.class);

    private final Map<String, Map<String, Parser<? extends Command>>> featureParsers = new TreeMap<>();
    private final Map<String, Map<String, HelpEntry>> featureHelpEntries = new TreeMap<>();

    /**
     * Creates an empty command registry.
     */
    public CommandRegistry() {}

    /**
     * Copies registrations so later changes to the source do not affect this registry.
     */
    CommandRegistry(CommandRegistry source) {
        requireNonNull(source);
        source.featureParsers.forEach((feature, parsers) -> featureParsers.put(feature, new TreeMap<>(parsers)));
        source.featureHelpEntries.forEach((feature, entries) ->
                featureHelpEntries.put(feature, new TreeMap<>(entries)));
    }

    /**
     * Registers a parser for {@code feature subcommand} without help metadata.
     * Prefer the overload that also takes a {@link HelpEntry} for user-facing commands.
     *
     * @param feature The feature keyword.
     * @param subcommand The subcommand keyword.
     * @param parser The feature's argument parser.
     * @throws IllegalArgumentException if keywords are invalid or the command is already registered.
     */
    public void register(String feature, String subcommand, Parser<? extends Command> parser) {
        registerParser(feature, subcommand, parser, null);
    }

    /**
     * Registers a parser and its help text for {@code feature subcommand}.
     * Keywords must be lowercase, and the help topic must match the feature keyword.
     *
     * @param feature The feature keyword.
     * @param subcommand The subcommand keyword.
     * @param parser The feature's argument parser.
     * @param helpEntry The help format, description, and examples for the command.
     * @throws IllegalArgumentException if keywords, topic, or command registration are invalid.
     */
    public void register(String feature, String subcommand, Parser<? extends Command> parser, HelpEntry helpEntry) {
        registerParser(feature, subcommand, parser, requireNonNull(helpEntry));
    }

    private void registerParser(String feature, String subcommand, Parser<? extends Command> parser,
            HelpEntry helpEntry) {
        requireNonNull(parser);
        checkArgument(TAssistParserUtil.isValidCommandKeyword(feature), "Feature keywords must be lowercase words.");
        checkArgument(TAssistParserUtil.isValidCommandKeyword(subcommand),
                "Subcommand keywords must be lowercase words.");
        checkArgument(helpEntry == null || feature.equals(helpEntry.getTopic()),
                "Help topic must match the feature keyword.");

        Map<String, Parser<? extends Command>> parsers = featureParsers.computeIfAbsent(feature,
                unused -> new TreeMap<>());
        checkArgument(!parsers.containsKey(subcommand), "Command already registered: " + feature + " " + subcommand);
        parsers.put(subcommand, parser);
        if (helpEntry != null) {
            featureHelpEntries.computeIfAbsent(feature, unused -> new TreeMap<>()).put(subcommand, helpEntry);
        }
        logger.fine("Registered command: " + feature + " " + subcommand);
    }

    /**
     * Returns whether the feature has any registered commands.
     */
    public boolean hasFeature(String feature) {
        requireNonNull(feature);
        return featureParsers.containsKey(feature);
    }

    /**
     * Returns help entries registered with feature commands, ordered by feature and subcommand.
     *
     * @return An immutable list of registered feature help entries.
     */
    public List<HelpEntry> getHelpEntries() {
        List<HelpEntry> entries = new ArrayList<>();
        featureHelpEntries.values().forEach(bySubcommand -> entries.addAll(bySubcommand.values()));
        return List.copyOf(entries);
    }

    /**
     * Dispatches the subcommand in {@code arguments} to its registered parser.
     * Leading and trailing whitespace and whitespace between keywords are ignored.
     *
     * @param feature The feature keyword extracted from user input.
     * @param arguments The remaining input, starting with the subcommand.
     * @return The parsed command.
     * @throws ParseException if the feature or subcommand is unknown, missing, or has invalid arguments.
     */
    public Command parseCommand(String feature, String arguments) throws ParseException {
        requireNonNull(feature);
        requireNonNull(arguments);
        Map<String, Parser<? extends Command>> parsers = featureParsers.get(feature);
        if (parsers == null) {
            throw new ParseException(String.format(MESSAGE_UNKNOWN_COMMAND, feature));
        }

        assert !parsers.isEmpty() : "Registered features must have at least one subcommand";
        String validSubcommands = String.join(", ", parsers.keySet());
        String trimmedArguments = arguments.trim();
        if (trimmedArguments.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_MISSING_SUBCOMMAND, feature, validSubcommands, feature));
        }

        String[] words = trimmedArguments.split("\\s+", 2);
        Parser<? extends Command> parser = parsers.get(words[0]);
        if (parser == null) {
            throw new ParseException(String.format(MESSAGE_UNKNOWN_SUBCOMMAND, words[0], feature, validSubcommands));
        }

        logger.fine("Dispatching command: " + feature + " " + words[0]);
        return parser.parse(words.length == 2 ? words[1] : "");
    }
}
