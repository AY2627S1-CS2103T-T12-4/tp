package seedu.address.commons.core;

/**
 * Describes the outcome of loading the application's saved data at startup.
 */
public enum DataLoadStatus {
    LOADED("", false),
    MISSING("No saved data found. Starting with an empty database.", false),
    FAILED("Saved data could not be read. No changes were made to the saved file.", true);

    private final String message;
    private final boolean isFailure;

    DataLoadStatus(String message, boolean isFailure) {
        this.message = message;
        this.isFailure = isFailure;
    }

    public String getMessage() {
        return message;
    }

    public boolean isFailure() {
        return isFailure;
    }
}
