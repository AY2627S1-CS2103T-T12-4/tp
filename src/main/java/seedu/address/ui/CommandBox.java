package seedu.address.ui;

import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";
    private static final PseudoClass INPUT_FOCUSED = PseudoClass.getPseudoClass("input-focused");

    private final CommandExecutor commandExecutor;
    private final CommandHistory commandHistory = new CommandHistory();

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
        commandTextField.focusedProperty().addListener((observable, wasFocused, isFocused) ->
                getRoot().pseudoClassStateChanged(INPUT_FOCUSED, isFocused));
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPressed);
    }

    /**
     * Returns keyboard focus to command input.
     */
    public void focus() {
        commandTextField.requestFocus();
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        commandHistory.add(commandText);
        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Recalls an earlier command on Up and a later one on Down, leaving other keys and modified Up and Down,
     * such as Shift+Up to select text, to the text field.
     */
    private void handleKeyPressed(KeyEvent event) {
        if (hasModifier(event)) {
            return;
        }

        if (event.getCode() == KeyCode.UP) {
            commandHistory.previous(commandTextField.getText()).ifPresent(this::showRecalledCommand);
            event.consume();
        } else if (event.getCode() == KeyCode.DOWN) {
            commandHistory.next().ifPresent(this::showRecalledCommand);
            event.consume();
        }
    }

    private static boolean hasModifier(KeyEvent event) {
        return event.isShiftDown() || event.isControlDown() || event.isAltDown() || event.isMetaDown();
    }

    /**
     * Shows {@code command} in the command box with the cursor at its end, ready to be edited.
     */
    private void showRecalledCommand(String command) {
        commandTextField.setText(command);
        commandTextField.end();
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.address.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }

}
