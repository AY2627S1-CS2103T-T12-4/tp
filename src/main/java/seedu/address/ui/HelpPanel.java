package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Shows an offline reference for commands available in this increment.
 */
public class HelpPanel extends UiPart<Region> {
    @FXML
    private VBox commands;

    /**
     * Creates the command reference.
     */
    public HelpPanel() {
        super("HelpPanel.fxml");
        addCommand("view students | groups | attendance | participation | assignments | help | storage",
                "Open a screen. Future features are labeled Coming soon.");
        addCommand("add n/NAME p/PHONE e/EMAIL a/ADDRESS [t/TAG]…", "Add a contact to the roster.");
        addCommand("list", "Show all contacts. Roster row numbers are the indices used by edit and delete.");
        addCommand("find KEYWORD [MORE_KEYWORDS]…", "Find contacts by name, ignoring case.");
        addCommand("edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…", "Edit a displayed contact.");
        addCommand("delete INDEX", "Delete the contact at the displayed row number.");
        addCommand("clear", "Delete every contact. This cannot be undone.");
        addCommand("help", "Show this reference. F1 also opens Help; Escape focuses the command box.");
        addCommand("exit", "Close TAssist.");
        addCommand("Example: find Alex", "Return to Students to inspect the matching records.");
        Label planned = new Label("Coming soon: tutorial groups, student IDs, attendance, participation and "
                + "assignments. Their domain commands are not available in this increment.");
        planned.setWrapText(true);
        planned.getStyleClass().add("coming-soon-note");
        commands.getChildren().add(planned);
    }

    private void addCommand(String syntax, String explanation) {
        Label command = new Label(syntax);
        command.setWrapText(true);
        command.getStyleClass().add("command-syntax");
        Label description = new Label(explanation);
        description.setWrapText(true);
        description.getStyleClass().add("muted");
        VBox row = new VBox(6, command, description);
        row.getStyleClass().add("command-reference");
        commands.getChildren().add(row);
    }
}
