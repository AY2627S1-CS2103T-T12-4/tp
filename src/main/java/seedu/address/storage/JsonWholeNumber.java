package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Converts a number read from the data file into a whole number, so a fractional mark is rejected
 * instead of being truncated.
 */
final class JsonWholeNumber {

    private JsonWholeNumber() {}

    /**
     * Returns {@code value} as an {@code int} if it has no fractional part and fits in an {@code int}.
     * A value written with a zero fractional part, such as {@code 5.0}, is read as {@code 5}.
     */
    static Optional<Integer> toInt(Number value) {
        requireNonNull(value);
        try {
            return Optional.of(new BigDecimal(value.toString()).intValueExact());
        } catch (NumberFormatException | ArithmeticException e) {
            return Optional.empty();
        }
    }
}
