package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses TAssist prefix-value parameters and checks each command's declared parameter rules.
 * Prefixes start at the beginning of input or after whitespace. Each allowed prefix may appear at most once.
 */
public class TAssistArgumentParser {
    public static final String MESSAGE_UNKNOWN_PREFIX = "Unknown prefix '%s'. Allowed prefixes: %s.";
    public static final String MESSAGE_MISSING_PARAMETERS =
            "Missing required parameter(s): %s. Include each exactly once.";
    public static final String MESSAGE_UNEXPECTED_TEXT =
            "Unexpected text before parameters: '%s'. Start each parameter with an allowed prefix: %s.";
    public static final String MESSAGE_NO_PARAMETERS = "This command takes no parameters. Remove the extra input.";

    private static final Pattern PREFIX_FORMAT =
            Pattern.compile("(?<!\\S)(?![A-Za-z][A-Za-z0-9+.-]*://)([^\\s/]+/)");

    private final List<Prefix> requiredPrefixes;
    private final Set<Prefix> allowedPrefixes;

    /**
     * Creates a parser with required and optional single-valued prefixes.
     *
     * @param requiredPrefixes The prefixes required exactly once.
     * @param optionalPrefixes The prefixes permitted at most once.
     * @throws IllegalArgumentException if prefixes are invalid, repeated, or both required and optional.
     */
    public TAssistArgumentParser(Collection<Prefix> requiredPrefixes, Collection<Prefix> optionalPrefixes) {
        requireNonNull(requiredPrefixes);
        requireNonNull(optionalPrefixes);
        this.requiredPrefixes = List.copyOf(requiredPrefixes);
        allowedPrefixes = new LinkedHashSet<>();
        for (Prefix prefix : requiredPrefixes) {
            addAllowedPrefix(prefix);
        }
        for (Prefix prefix : optionalPrefixes) {
            addAllowedPrefix(prefix);
        }
    }

    /**
     * Parses parameters and checks unknown prefixes, unexpected text, duplicates, and missing parameters.
     * Empty values are retained so a feature's value validator can report their constraints.
     *
     * @param arguments The command's arguments, excluding both command keywords.
     * @return The checked prefix-value mapping.
     * @throws ParseException if the arguments violate the declared parameter rules.
     */
    public ArgumentMultimap parse(String arguments) throws ParseException {
        requireNonNull(arguments);
        if (allowedPrefixes.isEmpty() && !arguments.trim().isEmpty()) {
            throw new ParseException(MESSAGE_NO_PARAMETERS);
        }

        ArgumentMultimap values = tokenize(arguments);
        if (!values.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_UNEXPECTED_TEXT, values.getPreamble(),
                    formatAllowedPrefixes()));
        }
        values.verifyNoDuplicatePrefixesFor(allowedPrefixes.toArray(Prefix[]::new));
        verifyRequiredPrefixes(values);
        return values;
    }

    private void addAllowedPrefix(Prefix prefix) {
        checkArgument(TAssistParserUtil.isValidPrefix(prefix), "Prefixes must be lowercase words followed by '/'.");
        checkArgument(allowedPrefixes.add(prefix), "Prefix declared more than once: " + prefix);
    }

    private ArgumentMultimap tokenize(String arguments) throws ParseException {
        ArgumentMultimap values = new ArgumentMultimap();
        Matcher matcher = PREFIX_FORMAT.matcher(arguments);
        Prefix currentPrefix = new Prefix("");
        int valueStart = 0;
        while (matcher.find()) {
            Prefix nextPrefix = new Prefix(matcher.group(1));
            if (!allowedPrefixes.contains(nextPrefix)) {
                throw new ParseException(String.format(MESSAGE_UNKNOWN_PREFIX, nextPrefix, formatAllowedPrefixes()));
            }
            values.put(currentPrefix, arguments.substring(valueStart, matcher.start()).trim());
            currentPrefix = nextPrefix;
            valueStart = matcher.end();
        }
        values.put(currentPrefix, arguments.substring(valueStart).trim());
        return values;
    }

    private void verifyRequiredPrefixes(ArgumentMultimap values) throws ParseException {
        String missingPrefixes = requiredPrefixes.stream()
                .filter(prefix -> values.getValue(prefix).isEmpty())
                .map(Prefix::toString)
                .collect(Collectors.joining(" "));
        if (!missingPrefixes.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_MISSING_PARAMETERS, missingPrefixes));
        }
    }

    private String formatAllowedPrefixes() {
        return allowedPrefixes.stream().map(Prefix::toString).collect(Collectors.joining(" "));
    }
}
