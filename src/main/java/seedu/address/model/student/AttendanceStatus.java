package seedu.address.model.student;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Locale;

/**
 * Represents whether a student attended a tutorial.
 */
public enum AttendanceStatus {
    PRESENT("present", "Present"),
    ABSENT("absent", "Absent");

    public static final String MESSAGE_CONSTRAINTS = "Invalid attendance status. Use present or absent.";

    private final String commandValue;
    private final String displayValue;

    AttendanceStatus(String commandValue, String displayValue) {
        this.commandValue = commandValue;
        this.displayValue = displayValue;
    }

    /**
     * Parses an attendance status, ignoring case and surrounding whitespace.
     *
     * @throws IllegalArgumentException if the value is neither present nor absent.
     */
    public static AttendanceStatus fromValue(String value) {
        String normalizedValue = requireNonNull(value).trim().toLowerCase(Locale.ROOT);
        checkArgument(normalizedValue.equals("present") || normalizedValue.equals("absent"), MESSAGE_CONSTRAINTS);
        return valueOf(normalizedValue.toUpperCase(Locale.ROOT));
    }

    public String getCommandValue() {
        return commandValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
