package seedu.address.ui;

import java.nio.file.Path;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * Shows the actual local storage path and the existing persistence behavior.
 */
public class StoragePanel extends UiPart<Region> {
    @FXML
    private Label filePath;

    /**
     * Creates the storage information panel for the configured data file.
     */
    public StoragePanel(Path path) {
        super("StoragePanel.fxml");
        filePath.setText(path.toAbsolutePath().normalize().toString());
    }
}
