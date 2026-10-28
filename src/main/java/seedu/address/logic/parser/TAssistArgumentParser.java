package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
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
    public static final String MESSAGE_EXTRA_TEXT =
            "Unexpected extra text after '%s': '%s'. This value cannot contain spaces.";
    public static final String MESSAGE_SEPARATE_LITERAL_WORD =
            " If %s is part of the value, put a space before and after it.";
    public static final String MESSAGE_SEPARATE_PREFIX = " If %s starts a new parameter, put a space before it.";
    public static final String MESSAGE_MISSING_SPACE =
            "Missing space before %s in '%s'. Put a space before each prefix.";

    private static final Pattern PREFIX_FORMAT =
            Pattern.compile("(?<!\\S)(?![A-Za-z][A-Za-z0-9+.-]*://)([^\\s/]+/)");
    private static final Pattern LITERAL_WORD_FORMAT = Pattern.compile("[^\\s/]+/[^\\s/]+");
    /** A word as the prefix pattern sees it: text up to the next whitespace. */
    private static final Pattern WORD_FORMAT = Pattern.compile("\\S*");

    private final List<Prefix> requiredPrefixes;
    private final Set<Prefix> allowedPrefixes;
    private final List<Prefix> singleWordPrefixes;
    /** Literal words in lowercase. */
    private final Set<String> literalWords;

    /**
     * Creates a parser with required and optional single-valued prefixes, none of which is limited to one word.
     *
     * @param requiredPrefixes The prefixes required exactly once.
     * @param optionalPrefixes The prefixes permitted at most once.
     * @throws IllegalArgumentException if prefixes are invalid, repeated, or both required and optional.
     */
    public TAssistArgumentParser(Collection<Prefix> requiredPrefixes, Collection<Prefix> optionalPrefixes) {
        this(requiredPrefixes, optionalPrefixes, List.of());
    }

    /**
     * Creates a parser with required and optional single-valued prefixes, some of which take a single word.
     *
     * @param requiredPrefixes The prefixes required exactly once.
     * @param optionalPrefixes The prefixes permitted at most once.
     * @param singleWordPrefixes The required or optional prefixes whose values cannot contain spaces.
     * @throws IllegalArgumentException if prefixes are invalid, repeated, both required and optional, or if a
     *         single-word prefix is not declared as required or optional.
     */
    public TAssistArgumentParser(Collection<Prefix> requiredPrefixes, Collection<Prefix> optionalPrefixes,
            Collection<Prefix> singleWordPrefixes) {
        this(requiredPrefixes, optionalPrefixes, singleWordPrefixes, List.of());
    }

    /**
     * Creates a parser as {@link #TAssistArgumentParser(Collection, Collection, Collection)} does, with words that
     * also stay part of a value.
     * A literal word, such as {@code s/o}, is text rather than a prefix when it stands alone between spaces.
     * Matching ignores case.
     *
     * @param requiredPrefixes The prefixes required exactly once.
     * @param optionalPrefixes The prefixes permitted at most once.
     * @param singleWordPrefixes The required or optional prefixes whose values cannot contain spaces.
     * @param literalWords The words that look like a prefix followed by text but belong to a value.
     * @throws IllegalArgumentException if prefixes are invalid, repeated, both required and optional, if a
     *         single-word prefix is not declared as required or optional, or if a literal word is not a single word
     *         with one slash inside it, or starts with an allowed prefix.
     */
    public TAssistArgumentParser(Collection<Prefix> requiredPrefixes, Collection<Prefix> optionalPrefixes,
            Collection<Prefix> singleWordPrefixes, Collection<String> literalWords) {
        requireNonNull(requiredPrefixes);
        requireNonNull(optionalPrefixes);
        requireNonNull(singleWordPrefixes);
        requireNonNull(literalWords);
        this.requiredPrefixes = List.copyOf(requiredPrefixes);
        allowedPrefixes = new LinkedHashSet<>();
        for (Prefix prefix : requiredPrefixes) {
            addAllowedPrefix(prefix);
        }
        for (Prefix prefix : optionalPrefixes) {
            addAllowedPrefix(prefix);
        }
        checkArgument(allowedPrefixes.containsAll(singleWordPrefixes),
                "Single-word prefixes must be declared as required or optional.");
        this.singleWordPrefixes = List.copyOf(singleWordPrefixes);
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
        ArgumentMultimap values = parseWithoutExtraTextCheck(arguments);
        verifyNoExtraText(values);
        return values;
    }

    /**
     * Parses parameters as {@link #parse(String)} does, except that it does not check for extra text after
     * single-word values. A caller that reports its own missing parameters must run those checks and then call
     * {@link #verifyNoExtraText(ArgumentMultimap)}, so that a missing parameter is reported before extra text.
     *
     * @param arguments The command's arguments, excluding both command keywords.
     * @return The checked prefix-value mapping.
     * @throws ParseException if the arguments violate the declared parameter rules other than extra text.
     */
    public ArgumentMultimap parseWithoutExtraTextCheck(String arguments) throws ParseException {
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

    /**
     * Checks that no single-word value of {@code values} is followed by extra text.
     *
     * @param values The mapping returned by {@link #parseWithoutExtraTextCheck(String)}.
     * @throws ParseException if a single-word value contains spaces.
     */
    public void verifyNoExtraText(ArgumentMultimap values) throws ParseException {
        for (Prefix prefix : singleWordPrefixes) {
            String[] words = values.getValue(prefix).orElse("").split("\\s+", 2);
            if (words.length > 1) {
                throw new ParseException(String.format(MESSAGE_EXTRA_TEXT, prefix + words[0], words[1]));
            }
        }
    }

    /**
     * Checks that {@code missingPrefix}, which has no value in {@code values}, was not typed right after another
     * value's text without a space, as {@code w/} is in {@code id/A0123456Xw/3}.
     *
     * @param values The mapping returned by {@link #parseWithoutExtraTextCheck(String)}.
     * @param missingPrefix The allowed prefix that has no value.
     * @throws ParseException if another value contains {@code missingPrefix} right after other text.
     */
    public void verifyNoMissingSpaceBefore(ArgumentMultimap values, Prefix missingPrefix) throws ParseException {
        requireNonNull(values);
        requireNonNull(missingPrefix);
        assert values.getValue(missingPrefix).isEmpty() : "Only a missing prefix can be joined to another value";
        Pattern joinedPrefix = Pattern.compile("\\S" + Pattern.quote(missingPrefix.getPrefix()));
        for (Prefix prefix : allowedPrefixes) {
            // A later word with a slash is read as a prefix of its own, so only a value's first word can hide one.
            String firstWord = values.getValue(prefix).orElse("").split("\\s+", 2)[0];
            if (joinedPrefix.matcher(firstWord).find()) {
                throw new ParseException(String.format(MESSAGE_MISSING_SPACE, missingPrefix, prefix + firstWord));
            }
        }
    }

    private void addAllowedPrefix(Prefix prefix) {
        checkArgument(TAssistParserUtil.isValidPrefix(prefix), "Prefixes must be lowercase words followed by '/'.");
        checkArgument(allowedPrefixes.add(prefix), "Prefix declared more than once: " + prefix);
    }

    private void addLiteralWord(String literalWord) {
        requireNonNull(literalWord);
        checkArgument(LITERAL_WORD_FORMAT.matcher(literalWord).matches(),
                "Literal words must be one word with a slash inside it: " + literalWord);
        Prefix wordPrefix = new Prefix(literalWord.substring(0, literalWord.indexOf('/') + 1)
                .toLowerCase(Locale.ROOT));
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
                throw new ParseException(createUnknownPrefixMessage(nextPrefix, word, arguments));
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
        Matcher word = WORD_FORMAT.matcher(arguments).region(start, arguments.length());
        boolean isFound = word.lookingAt();
        assert isFound : "A possibly empty word always matches";
        return word.group();
    }

    /**
     * Returns the error for the unknown {@code prefix} that starts {@code word} in {@code arguments}. If the word
     * starts with a literal word glued to a name, such as {@code s/oKumar}, the error also says to separate the
     * literal word. Text that continues in lowercase, as in {@code a/lee} for the literal word {@code a/l}, is not
     * taken as a glued name. Otherwise, the error may add a hint from {@link #findJoinedPrefixHint(Prefix, String)}.
     */
    private String createUnknownPrefixMessage(Prefix prefix, String word, String arguments) {
        String message = String.format(MESSAGE_UNKNOWN_PREFIX, prefix, formatAllowedPrefixes());
        Optional<String> literalWordHint = literalWords.stream()
                .filter(literalWord -> isGluedToName(word, literalWord))
                .findFirst()
                .map(literalWord -> String.format(MESSAGE_SEPARATE_LITERAL_WORD,
                        word.substring(0, literalWord.length())));
        return message + literalWordHint.or(() -> findJoinedPrefixHint(prefix, arguments)).orElse("");
    }

    /**
     * Returns a hint to put a space before the allowed prefix that ends the unknown {@code prefix}, as {@code id/}
     * ends {@code Tanid/}, if {@code arguments} has no parameter with that allowed prefix.
     */
    private Optional<String> findJoinedPrefixHint(Prefix prefix, String arguments) {
        String unknownPrefix = prefix.getPrefix();
        return allowedPrefixes.stream()
                .filter(allowedPrefix -> unknownPrefix.length() > allowedPrefix.getPrefix().length()
                        && unknownPrefix.endsWith(allowedPrefix.getPrefix()))
                .filter(allowedPrefix -> !hasParameter(arguments, allowedPrefix))
                .findFirst()
                .map(allowedPrefix -> String.format(MESSAGE_SEPARATE_PREFIX, allowedPrefix));
    }

    private static boolean hasParameter(String arguments, Prefix prefix) {
        return Pattern.compile("(?<!\\S)" + Pattern.quote(prefix.getPrefix())).matcher(arguments).find();
    }

    private static boolean isGluedToName(String word, String literalWord) {
        if (!word.toLowerCase(Locale.ROOT).startsWith(literalWord) || word.length() == literalWord.length()) {
            return false;
        }
        return !Character.isLowerCase(word.charAt(literalWord.length()));
    }

    private void verifyRequiredPrefixes(ArgumentMultimap values) throws ParseException {
        List<Prefix> missingPrefixes = requiredPrefixes.stream()
                .filter(prefix -> values.getValue(prefix).isEmpty())
                .toList();
        for (Prefix missingPrefix : missingPrefixes) {
            verifyNoMissingSpaceBefore(values, missingPrefix);
        }
        if (!missingPrefixes.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_MISSING_PARAMETERS, missingPrefixes.stream()
                    .map(Prefix::toString)
                    .collect(Collectors.joining(" "))));
        }
    }

    private String formatAllowedPrefixes() {
        return allowedPrefixes.stream().map(Prefix::toString).collect(Collectors.joining(" "));
    }
}
