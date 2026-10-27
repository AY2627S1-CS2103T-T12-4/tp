package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;

public class TAssistArgumentParserTest {
    private static final Prefix PREFIX_NAME = new Prefix("n/");
    private static final Prefix PREFIX_ID = new Prefix("id/");
    private static final Prefix PREFIX_GROUP = new Prefix("g/");

    private final TAssistArgumentParser parser = new TAssistArgumentParser(
            List.of(PREFIX_NAME, PREFIX_ID), List.of(PREFIX_GROUP));

    @Test
    public void parse_validParameters_trimsValuesAndAllowsAnyOrder() throws Exception {
        ArgumentMultimap values = parser.parse("  id/a123\t n/ Alice   Tan \n g/T01  ");
        assertEquals("a123", values.getValue(PREFIX_ID).orElseThrow());
        assertEquals("Alice   Tan", values.getValue(PREFIX_NAME).orElseThrow());
        assertEquals("T01", values.getValue(PREFIX_GROUP).orElseThrow());
        assertEquals("", values.getPreamble());
    }

    @Test
    public void parse_optionalParameterAbsent_success() throws Exception {
        assertTrue(parser.parse("n/Alice id/A1").getValue(PREFIX_GROUP).isEmpty());
    }

    @Test
    public void parse_emptyValue_retainsValueForDomainValidation() throws Exception {
        assertEquals("", parser.parse("n/ id/A1").getValue(PREFIX_NAME).orElseThrow());
    }

    @Test
    public void parse_slashesWithinValues_preservesValues() throws Exception {
        ArgumentMultimap values = parser.parse("n/PS1/PS2 https://example.com id/A1");
        assertEquals("PS1/PS2 https://example.com", values.getValue(PREFIX_NAME).orElseThrow());
        assertEquals("/", parser.parse("n// id/A1").getValue(PREFIX_NAME).orElseThrow());
    }

    @Test
    public void parse_unknownOrUppercasePrefix_throwsSpecificError() {
        for (String prefix : new String[] {"x/", "N/", "unknown/", "1/", "bad-prefix/"}) {
            String message = String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, prefix, "n/ id/ g/");
            assertThrows(ParseException.class, message, () -> parser.parse("n/Alice " + prefix + "value id/A1"));
        }
    }

    @Test
    public void parse_missingParameters_namesMissingPrefixes() {
        assertMissingParameters("", "n/ id/");
        assertMissingParameters("g/T01", "n/ id/");
        assertMissingParameters("id/A1", "n/");
        assertMissingParameters("n/Alice", "id/");
    }

    @Test
    public void parse_repeatedRequiredOrOptionalParameter_throwsSpecificError() {
        for (Prefix prefix : List.of(PREFIX_NAME, PREFIX_ID, PREFIX_GROUP)) {
            String input = "n/Alice id/A1 g/T01 " + prefix + "Other";
            assertThrows(ParseException.class, Messages.getErrorMessageForDuplicatePrefixes(prefix), () ->
                    parser.parse(input));
        }
    }

    @Test
    public void parse_unexpectedPreamble_throwsSpecificError() {
        String message = String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "extra", "n/ id/ g/");
        assertThrows(ParseException.class, message, () -> parser.parse("extra n/Alice id/A1"));
    }

    @Test
    public void parse_noParametersAllowed_rejectsAllExtraInput() throws Exception {
        TAssistArgumentParser noParametersParser = new TAssistArgumentParser(List.of(), List.of());
        assertEquals("", noParametersParser.parse(" \t ").getPreamble());
        for (String input : new String[] {"extra", "n/Alice"}) {
            assertThrows(ParseException.class, TAssistArgumentParser.MESSAGE_NO_PARAMETERS, () ->
                    noParametersParser.parse(input));
        }
    }

    @Test
    public void parse_literalWords_keptInValueIgnoringCase() throws Exception {
        TAssistArgumentParser literalParser = new TAssistArgumentParser(
                List.of(PREFIX_NAME, PREFIX_ID), List.of(), List.of("s/o", "D/O"));

        ArgumentMultimap values = literalParser.parse("n/Ravi s/o Kumar id/A1");
        assertEquals("Ravi s/o Kumar", values.getValue(PREFIX_NAME).orElseThrow());
        assertEquals("A1", values.getValue(PREFIX_ID).orElseThrow());
        assertEquals("SITI D/O AHMAD s/o X", literalParser.parse("id/A1 n/SITI D/O AHMAD s/o X")
                .getValue(PREFIX_NAME).orElseThrow());
        assertEquals("Ravi\ts/o\tKumar", literalParser.parse("n/Ravi\ts/o\tKumar id/A1")
                .getValue(PREFIX_NAME).orElseThrow());
    }

    @Test
    public void parse_literalWordNotStandingAlone_throwsUnknownPrefix() {
        TAssistArgumentParser literalParser = new TAssistArgumentParser(
                List.of(PREFIX_NAME, PREFIX_ID), List.of(), List.of("s/o"));
        for (String input : new String[] {"n/Ravi s/oKumar id/A1", "n/Ravi s/ o Kumar id/A1",
            "n/Ravi s/o/ Kumar id/A1", "n/Ravi x/o Kumar id/A1"}) {
            String prefix = input.contains("x/") ? "x/" : "s/";
            assertThrows(ParseException.class, String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, prefix,
                    "n/ id/"), () -> literalParser.parse(input));
        }
        // Without declared literal words, s/o is still an unknown prefix.
        assertThrows(ParseException.class, String.format(TAssistArgumentParser.MESSAGE_UNKNOWN_PREFIX, "s/",
                "n/ id/ g/"), () -> parser.parse("n/Ravi s/o Kumar id/A1"));
    }

    @Test
    public void parse_literalWordBeforeFirstPrefix_throwsUnexpectedText() {
        TAssistArgumentParser literalParser = new TAssistArgumentParser(
                List.of(PREFIX_NAME), List.of(), List.of("s/o"));
        assertThrows(ParseException.class, String.format(TAssistArgumentParser.MESSAGE_UNEXPECTED_TEXT, "s/o",
                "n/"), () -> literalParser.parse("s/o n/Kumar"));
    }

    @Test
    public void constructor_invalidLiteralWords_throwsIllegalArgumentException() {
        for (String literalWord : new String[] {"", "so", "s/", "/o", "s/o/x", "s o/x", "n/o"}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new TAssistArgumentParser(List.of(PREFIX_NAME), List.of(), List.of(literalWord)));
        }
    }

    @Test
    public void constructor_invalidOrRepeatedPrefixes_throwsIllegalArgumentException() {
        for (String prefix : new String[] {"", "N/", "n", "n /", "1/"}) {
            assertThrows(IllegalArgumentException.class, () ->
                    new TAssistArgumentParser(List.of(new Prefix(prefix)), List.of()));
        }
        assertThrows(IllegalArgumentException.class, () ->
                new TAssistArgumentParser(List.of(PREFIX_NAME, PREFIX_NAME), List.of()));
        assertThrows(IllegalArgumentException.class, () ->
                new TAssistArgumentParser(List.of(PREFIX_NAME), List.of(PREFIX_NAME)));
        assertThrows(IllegalArgumentException.class, () ->
                new TAssistArgumentParser(List.of(), List.of(PREFIX_NAME, PREFIX_NAME)));
    }

    @Test
    public void constructor_sourceModified_keepsDeclaredPrefixes() throws Exception {
        List<Prefix> requiredPrefixes = new ArrayList<>(List.of(PREFIX_NAME));
        TAssistArgumentParser copiedParser = new TAssistArgumentParser(requiredPrefixes, List.of());
        requiredPrefixes.clear();
        assertEquals("Alice", copiedParser.parse("n/Alice").getValue(PREFIX_NAME).orElseThrow());
        assertThrows(ParseException.class, () -> copiedParser.parse(""));
    }

    @Test
    public void nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TAssistArgumentParser(null, List.of()));
        assertThrows(NullPointerException.class, () -> new TAssistArgumentParser(List.of(), null));
        assertThrows(NullPointerException.class, () -> new TAssistArgumentParser(List.of(), List.of(), null));
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    private void assertMissingParameters(String input, String missingPrefixes) {
        assertThrows(ParseException.class, String.format(TAssistArgumentParser.MESSAGE_MISSING_PARAMETERS,
                missingPrefixes), () -> parser.parse(input));
    }
}
