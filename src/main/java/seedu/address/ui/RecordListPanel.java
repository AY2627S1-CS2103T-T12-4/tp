package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Region;

/**
 * Shows the most recently requested record listing in the main workspace.
 */
public class RecordListPanel extends UiPart<Region> {
    @FXML
    private TextArea recordListing;

    /**
     * Creates an initially empty records screen.
     */
    public RecordListPanel() {
        super("RecordListPanel.fxml");
    }

    /**
     * Displays a complete listing, starting at its first row.
     */
    public void setListing(String listing) {
        recordListing.setText(requireNonNull(listing));
        recordListing.positionCaret(0);
        recordListing.setScrollTop(0);
    }

    /**
     * Scrolls the listing by a page, retaining a small overlap for context.
     */
    public void scrollPage(boolean isDown) {
        double page = recordListing.getHeight() * 0.9;
        recordListing.setScrollTop(Math.max(0, recordListing.getScrollTop() + (isDown ? page : -page)));
    }
}
