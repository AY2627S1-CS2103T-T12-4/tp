package seedu.address.ui;

import java.nio.file.Path;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;

/**
 * A UI for the status bar that is displayed at the footer of the application.
 */
public class StatusBarFooter extends UiPart<Region> {

    private static final String FXML = "StatusBarFooter.fxml";
    private static final PseudoClass FAILURE = PseudoClass.getPseudoClass("failure");

    private final Path saveLocation;

    @FXML
    private Label saveLocationStatus;

    /**
     * Creates a {@code StatusBarFooter} with the given {@code Path}.
     */
    public StatusBarFooter(Path saveLocation) {
        super(FXML);
        this.saveLocation = saveLocation;
        setStatus("Local data file");
    }

    /**
     * Shows the latest command status alongside the configured file path.
     */
    public void setStatus(String status) {
        setStatus(status, false);
    }

    /**
     * Shows the latest command status alongside the configured file path, with a red dot if the command failed.
     */
    public void setStatus(String status, boolean isFailure) {
        getRoot().pseudoClassStateChanged(FAILURE, isFailure);
        String text = status + " · " + saveLocation;
        saveLocationStatus.setText(text);
        saveLocationStatus.setTooltip(new Tooltip(text));
    }

}
