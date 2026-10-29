package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.core.WorkspaceView;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    private final WorkspaceView view;

    /** Help topic to show first, or an empty string to show Help from the top. */
    private final String helpTopic;

    private final boolean hasSavedChanges;

    /** The user cancelled the command, so its input should be kept for editing. */
    private final boolean isCancelled;

    private final boolean isRecordListing;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, boolean showHelp, boolean exit) {
        this(feedbackToUser, showHelp, exit, null, "");
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, WorkspaceView view,
            String helpTopic) {
        this(feedbackToUser, showHelp, exit, view, helpTopic, false);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, WorkspaceView view,
            String helpTopic, boolean hasSavedChanges) {
        this(feedbackToUser, showHelp, exit, view, helpTopic, hasSavedChanges, false);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, WorkspaceView view,
            String helpTopic, boolean hasSavedChanges, boolean isCancelled) {
        this(feedbackToUser, showHelp, exit, view, helpTopic, hasSavedChanges, isCancelled, false);
    }

    private CommandResult(String feedbackToUser, boolean showHelp, boolean exit, WorkspaceView view,
            String helpTopic, boolean hasSavedChanges, boolean isCancelled, boolean isRecordListing) {
        this.feedbackToUser = requireNonNull(feedbackToUser);
        this.showHelp = showHelp;
        this.exit = exit;
        this.view = view;
        this.helpTopic = requireNonNull(helpTopic);
        this.hasSavedChanges = hasSavedChanges;
        this.isCancelled = isCancelled;
        this.isRecordListing = isRecordListing;
    }

    /**
     * Constructs a result that requests a workspace screen.
     */
    public CommandResult(String feedbackToUser, WorkspaceView view) {
        this(feedbackToUser, false, false, requireNonNull(view), "");
    }

    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, false, false);
    }

    /**
     * Returns a result that opens Help at the commands of {@code helpTopic}.
     * An empty {@code helpTopic} opens Help from the top.
     */
    public static CommandResult forHelp(String feedbackToUser, String helpTopic) {
        return new CommandResult(feedbackToUser, true, false, null, helpTopic);
    }

    /**
     * Returns a complete listing to display in the main records workspace.
     */
    public static CommandResult forRecordListing(String listing) {
        return new CommandResult(listing, false, false, WorkspaceView.RECORDS, "", false, false, true);
    }

    /**
     * Returns a result for a command the user cancelled before it ran, so that the command stays in the
     * command box for editing.
     */
    public static CommandResult forCancelled(String feedbackToUser) {
        return new CommandResult(feedbackToUser, false, false, null, "", false, true);
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    public Optional<WorkspaceView> getView() {
        return Optional.ofNullable(view);
    }

    public String getHelpTopic() {
        return helpTopic;
    }

    public boolean hasSavedChanges() {
        return hasSavedChanges;
    }

    public boolean isRecordListing() {
        return isRecordListing;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Returns a copy confirming that this command's data changes have been saved.
     */
    public CommandResult withSavedChanges() {
        return new CommandResult(feedbackToUser, showHelp, exit, view, helpTopic, true, isCancelled, isRecordListing);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult otherCommandResult)) {
            return false;
        }

        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit
                && view == otherCommandResult.view
                && helpTopic.equals(otherCommandResult.helpTopic)
                && hasSavedChanges == otherCommandResult.hasSavedChanges
                && isCancelled == otherCommandResult.isCancelled
                && isRecordListing == otherCommandResult.isRecordListing;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, showHelp, exit, view, helpTopic, hasSavedChanges, isCancelled,
                isRecordListing);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .add("view", view)
                .add("helpTopic", helpTopic)
                .add("hasSavedChanges", hasSavedChanges)
                .add("isCancelled", isCancelled)
                .add("isRecordListing", isRecordListing)
                .toString();
    }

}
