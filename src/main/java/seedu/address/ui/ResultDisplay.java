package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * A UI component that displays the result of a command execution.
 * It grows to fit the message, so that normal messages can be read without scrolling.
 */
public class ResultDisplay extends UiPart<Region> {
    static final int MAX_VISIBLE_LINES = 6;

    private static final String FXML = "ResultDisplay.fxml";
    /** Width of the text area that text cannot use: its padding and a vertical scroll bar. */
    private static final double NON_TEXT_WIDTH = 30;

    /** Measures how many lines a message wraps to. It is never shown. */
    private final Text measurement = new Text();

    @FXML
    private TextArea resultDisplay;
    @FXML
    private StackPane feedbackPane;

    /**
     * Creates the command feedback area.
     */
    public ResultDisplay() {
        super(FXML);
        resultDisplay.textProperty().addListener(observable -> fitToText());
        resultDisplay.widthProperty().addListener(observable -> fitToText());
        resultDisplay.fontProperty().addListener(observable -> fitToText());
        fitToText();
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

    /**
     * Shows as many lines as the message needs, up to {@link #MAX_VISIBLE_LINES}. Longer messages scroll.
     */
    private void fitToText() {
        resultDisplay.setPrefRowCount(Math.clamp(countLines(), 1, MAX_VISIBLE_LINES));
    }

    /**
     * Returns the number of lines the message takes up at the current width, including wrapped lines.
     */
    int countLines() {
        measurement.setFont(resultDisplay.getFont());
        measurement.setWrappingWidth(Math.max(0, resultDisplay.getWidth() - NON_TEXT_WIDTH));
        measurement.setText("X");
        double lineHeight = measurement.getLayoutBounds().getHeight();
        measurement.setText(resultDisplay.getText());
        double textHeight = measurement.getLayoutBounds().getHeight();
        assert lineHeight > 0 : "A line of text must have a height";
        return (int) Math.round(textHeight / lineHeight);
    }
}
