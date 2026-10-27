package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses TAssist prefix-value parameters and checks each command's declared parameter rules.
 * Prefixes start at the beginning of input or after whitespace. Each allowed prefix may appear at most once.
 * A command may also declare literal words, such as {@code s/o} in a name, that look like a prefix followed by text
 * but belong to a value.
 */
public class TAssistArgumentParser {
    public static final String MESSAGE_UNKNOWN_PREFIX = "Unknown prefix '%s'. Allowed prefixes: %s.";
    public static final String MESSAGE_MISSING_PARAMETERS =
            "Missing required parameter(s): %s. Include each exactly once.";
    public static final String MESSAGE_UNEXPECTED_TEXT =
            "Unexpected text before parameters: '%s'. Start each parameter with an allowed prefix: %s.";
    public static final String MESSAGE_NO_PARAMETERS = "This command takes no parameters. Remove the extra input.";
    public static final String MESSAGE_SEPARATE_LITERAL_WORD =
            " If %s is part of the value, put a space before and after it.";

    private static final Pattern PREFIX_FORMAT =
            Pattern.compile("(?<!\\S)(?![A-Za-z][A-Za-z0-9+.-]*://)([^\\s/]+/)");
    private static final Pattern LITERAL_WORD_FORMAT = Pattern.compile("[^\\s/]+/[^\\s/]+");

    private final List<Prefix> requiredPrefixes;
    private final Set<Prefix> allowedPrefixes;
    /** Literal words in lowercase. */
    private final Set<String> literalWords;

    /**
     * Creates a parser with required and optional single-valued prefixes.
     *
     * @param requiredPrefixes The prefixes required exactly once.
     * @param optionalPrefixes The prefixes permitted at most once.
     * @throws IllegalArgumentException if prefixes are invalid, repeated, or both required and optional.
     */
    public TAssistArgumentParser(Collection<Prefix> requiredPrefixes, Collection<Prefix> optionalPrefixes) {
        this(requiredPrefixes, optionalPrefixes, List.of());
    }

    /**
     * Creates a parser with required and optional single-valued prefixes, and words that stay part of a value.
     * A literal word, such as {@code s/o}, is text rather than a prefix when it stands alone between spaces.
     * Matching ignores case.
     *
     * @param requiredPrefixes The prefixes required exactly once.
     * @param optionalPrefixes The prefixes permitted at most once.
     * @param literalWords The words that look like a prefix followed by text but belong to a value.
     * @throws IllegalArgumentException if prefixes are invalid, repeated, or both required and optional, or if a
     *     literal word is not a single word with one slash inside it, or starts with an allowed prefix.
     */
    public TAssistArgumentParser(Collection<Prefix> requiredPrefixes, Collection<Prefix> optionalPrefixes,
            Collection<String> literalWords) {
        requireNonNull(requiredPrefixes);
        requireNonNull(optionalPrefixes);
        requireNonNull(literalWords);
        this.requiredPrefixes = List.copyOf(requiredPrefixes);
        allowedPrefixes = new LinkedHashSet<>();
        for (Prefix prefix : requiredPrefixes) {
            addAllowedPrefix(prefix);
        }
        for (Prefix prefix : optionalPrefixes) {
            addAllowedPrefix(prefix);
        }
        this.literalWords = new LinkedHashSet<>();
        for (String literalWord : literalWords) {
            addLiteralWord(literalWord);
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

    private void addLiteralWord(String literalWord) {
        requireNonNull(literalWord);
        checkArgument(LITERAL_WORD_FORMAT.matcher(literalWord).matches(),
                "Literal words must be one word with a slash inside it: " + literalWord);
        Prefix wordPrefix = new Prefix(literalWord.substring(0, literalWord.indexOf('/') + 1));
        checkArgument(!allowedPrefixes.contains(wordPrefix),
                "Literal word starts with an allowed prefix: " + literalWord);
        literalWords.add(literalWord.toLowerCase(Locale.ROOT));
    }

    private ArgumentMultimap tokenize(String arguments) throws ParseException {
        ArgumentMultimap values = new ArgumentMultimap();
        Matcher matcher = PREFIX_FORMAT.matcher(arguments);
        Prefix currentPrefix = new Prefix("");
        int valueStart = 0;
        while (matcher.find()) {
            Prefix nextPrefix = new Prefix(matcher.group(1));
            if (!allowedPrefixes.contains(nextPrefix)) {
                String word = getWordAt(arguments, matcher.start());
                if (literalWords.contains(word.toLowerCase(Locale.ROOT))) {
                    continue;
                }
                throw new ParseException(createUnknownPrefixMessage(nextPrefix, word));
            }
            values.put(currentPrefix, arguments.substring(valueStart, matcher.start()).trim());
            currentPrefix = nextPrefix;
            valueStart = matcher.end();
        }
        values.put(currentPrefix, arguments.substring(valueStart).trim());
        return values;
    }

    /**
     * Returns the whitespace-delimited word of {@code arguments} that starts at {@code start}.
     */
    private static String getWordAt(String arguments, int start) {
        int end = start;
        while (end < arguments.length() && !Character.isWhitespace(arguments.charAt(end))) {
            end++;
        }
        return arguments.substring(start, end);
    }

    /**
     * Returns the error for the unknown {@code prefix} that starts {@code word}. If the word starts with a literal
     * word joined to more text, such as {@code s/oKumar}, the error also says to separate the literal word.
     */
    private String createUnknownPrefixMessage(Prefix prefix, String word) {
        String message = String.format(MESSAGE_UNKNOWN_PREFIX, prefix, formatAllowedPrefixes());
        String lowerCaseWord = word.toLowerCase(Locale.ROOT);
        return literalWords.stream()
                .filter(lowerCaseWord::startsWith)
                .findFirst()
                .map(literalWord -> message + String.format(MESSAGE_SEPARATE_LITERAL_WORD,
                        word.substring(0, literalWord.length())))
                .orElse(message);
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
