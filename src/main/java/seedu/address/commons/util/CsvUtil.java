package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Formats values as comma-separated values (CSV) that spreadsheets open correctly.
 * Follows RFC 4180: rows end with CRLF, and a field that contains a comma, a double quote or a line break is
 * enclosed in double quotes, with each double quote inside it doubled.
 */
public final class CsvUtil {
    /** Line separator between rows, as RFC 4180 requires. */
    public static final String ROW_SEPARATOR = "\r\n";

    private static final char FIELD_SEPARATOR = ',';
    private static final char QUOTE = '"';
    /** First characters that make a spreadsheet read a field as a formula. */
    private static final String FORMULA_STARTS = "=+-@\t\r";

    private CsvUtil() {}

    /**
     * Returns {@code rows} as CSV text, with every row, including the last, ended by {@link #ROW_SEPARATOR}.
     */
    public static String formatTable(List<List<String>> rows) {
        requireNonNull(rows);
        return rows.stream().map(row -> formatRow(row) + ROW_SEPARATOR).collect(Collectors.joining());
    }

    /**
     * Returns {@code fields} as one CSV row, without a line separator.
     */
    public static String formatRow(List<String> fields) {
        requireNonNull(fields);
        return fields.stream().map(CsvUtil::formatField).collect(Collectors.joining(String.valueOf(FIELD_SEPARATOR)));
    }

    /**
     * Returns {@code value} as one CSV field.
     * A value that a spreadsheet would run as a formula, such as {@code =SUM(A1)}, is prefixed with an apostrophe so
     * that it is shown as text instead. A value is quoted only when it has to be.
     */
    static String formatField(String value) {
        requireNonNull(value);
        String safeValue = startsLikeFormula(value) ? "'" + value : value;
        if (!needsQuotes(safeValue)) {
            return safeValue;
        }
        String escapedQuotes = safeValue.replace(String.valueOf(QUOTE), String.valueOf(QUOTE) + QUOTE);
        return QUOTE + escapedQuotes + QUOTE;
    }

    private static boolean startsLikeFormula(String value) {
        return !value.isEmpty() && FORMULA_STARTS.indexOf(value.charAt(0)) >= 0;
    }

    private static boolean needsQuotes(String value) {
        return value.indexOf(FIELD_SEPARATOR) >= 0 || value.indexOf(QUOTE) >= 0
                || value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0
                || !value.equals(value.strip());
    }
}
