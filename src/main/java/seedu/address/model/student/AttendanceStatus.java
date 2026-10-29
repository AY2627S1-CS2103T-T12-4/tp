package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

import java.util.Locale;

/**
 * Represents whether a student attended a tutorial, arrived late, or was absent with or without a valid reason.
 */
public enum AttendanceStatus {
    PRESENT("present", "Present"),
    ABSENT("absent", "Absent"),
    LATE("late", "Late"),
    EXCUSED("excused", "Excused");

    public static final String MESSAGE_CONSTRAINTS =
            "Invalid attendance status. Use present, absent, late, or excused.";

    private final String commandValue;
    private final String displayValue;

    AttendanceStatus(String commandValue, String displayValue) {
        this.commandValue = commandValue;
        this.displayValue = displayValue;
    }

    /**
     * Parses an attendance status, ignoring case and surrounding whitespace.
     *
     * @throws IllegalArgumentException if the value is not present, absent, late, or excused.
     */
    public static AttendanceStatus fromValue(String value) {
        String normalizedValue = requireNonNull(value).trim().toLowerCase(Locale.ROOT);
        for (AttendanceStatus status : values()) {
            if (status.commandValue.equals(normalizedValue)) {
                return status;
            }
        }
        throw new IllegalArgumentException(MESSAGE_CONSTRAINTS);
    }

    /**
     * Returns how a stored attendance value is shown to the user, such as {@code Present} for {@code PRESENT}.
     * {@code PRESENT}, {@code ABSENT}, {@code LATE}, and {@code EXCUSED} are the values that commands and the data
     * file store. Any other stored text is returned unchanged, so that it is shown as it is instead of failing.
     */
    public static String formatStoredValue(String storedValue) {
        requireNonNull(storedValue);
        for (AttendanceStatus status : values()) {
            if (status.name().equals(storedValue)) {
                return status.getDisplayValue();
            }
        }
        return storedValue;
    }

    public String getCommandValue() {
        return commandValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
