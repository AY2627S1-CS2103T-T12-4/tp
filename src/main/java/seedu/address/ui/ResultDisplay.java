package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    @FXML
    private TextArea resultDisplay;
    @FXML
    private StackPane feedbackPane;

    /**
     * Creates the command feedback area.
     */
    public ResultDisplay() {
        super(FXML);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        setFeedbackToUser(feedbackToUser, false);
    }

    /**
     * Displays command feedback with an explicit failure state.
     */
    public void setFeedbackToUser(String feedbackToUser, boolean isFailure) {
        requireNonNull(feedbackToUser);
        feedbackPane.pseudoClassStateChanged(PseudoClass.getPseudoClass("failure"), isFailure);
        resultDisplay.setText(feedbackToUser);
    }

}
