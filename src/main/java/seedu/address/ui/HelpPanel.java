package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.logic.help.HelpEntry;

/**
 * Shows an offline reference for commands available in this increment.
 */
public class HelpPanel extends UiPart<Region> {
    @FXML
    private VBox commands;

    /**
     * Creates the command reference from registered help entries.
     *
     * @param helpEntries The commands that should appear in the reference.
     */
    public HelpPanel(List<HelpEntry> helpEntries) {
        super("HelpPanel.fxml");
        String previousTopic = null;
        for (HelpEntry entry : requireNonNull(helpEntries)) {
            requireNonNull(entry);
            if (!entry.getTopic().equals(previousTopic)) {
                addTopicHeading(entry.getTopic().isEmpty() ? "Current commands" : titleCase(entry.getTopic()));
                previousTopic = entry.getTopic();
            }
            addCommand(entry);
        }
        Label planned = new Label("Coming soon: tutorial groups, student IDs, attendance, participation and "
                + "assignments. Their domain commands are not available in this increment.");
        planned.setWrapText(true);
        planned.getStyleClass().add("coming-soon-note");
        commands.getChildren().add(planned);
    }

    private void addTopicHeading(String topic) {
        Label heading = new Label(topic);
        heading.getStyleClass().add("section-title");
        commands.getChildren().add(heading);
    }

    private String titleCase(String topic) {
        return Character.toUpperCase(topic.charAt(0)) + topic.substring(1) + " commands";
    }

    private void addCommand(HelpEntry entry) {
        Label command = new Label(entry.getCommandFormat());
        command.setWrapText(true);
        command.getStyleClass().add("command-syntax");
        StringBuilder explanation = new StringBuilder(entry.getDescription());
        for (String example : entry.getExamples()) {
            explanation.append("\nExample: ").append(example);
        }
        Label description = new Label(explanation.toString());
        description.setWrapText(true);
        description.getStyleClass().add("muted");
        VBox row = new VBox(6, command, description);
        row.getStyleClass().add("command-reference");
        commands.getChildren().add(row);
    }
}
