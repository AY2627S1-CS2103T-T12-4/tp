package seedu.address.commons.core;

import static java.util.Objects.requireNonNull;

/**
 * Describes the outcome of loading the application's saved data at startup.
 */
public final class DataLoadStatus {
    public static final String MESSAGE_FAILED_PREFIX = "Saved data could not be read.";
    public static final String MESSAGE_FILE_UNCHANGED = "No changes were made to the saved file.";

    public static final DataLoadStatus LOADED = new DataLoadStatus("", false);
    public static final DataLoadStatus MISSING =
            new DataLoadStatus("No saved data found. Starting with an empty database.", false);
    public static final DataLoadStatus FAILED =
            new DataLoadStatus(MESSAGE_FAILED_PREFIX + " " + MESSAGE_FILE_UNCHANGED, true);

    private final String message;
    private final boolean isFailure;

    private DataLoadStatus(String message, boolean isFailure) {
        this.message = message;
        this.isFailure = isFailure;
    }

    /**
     * Returns a failed outcome whose message names the {@code reason} the saved data was rejected.
     */
    public static DataLoadStatus failed(String reason) {
        requireNonNull(reason);
        if (reason.isBlank()) {
            return FAILED;
        }
        return new DataLoadStatus(MESSAGE_FAILED_PREFIX + " " + reason.strip() + " " + MESSAGE_FILE_UNCHANGED, true);
    }

    public String getMessage() {
        return message;
    }

    public boolean isFailure() {
        return isFailure;
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || other instanceof DataLoadStatus otherStatus
                && message.equals(otherStatus.message)
                && isFailure == otherStatus.isFailure;
    }

    @Override
    public int hashCode() {
        return message.hashCode();
    }

    @Override
    public String toString() {
        return message;
    }
}
