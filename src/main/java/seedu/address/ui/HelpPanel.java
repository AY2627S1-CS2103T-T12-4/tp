package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;

/**
 * Shows an offline reference for commands available in this increment.
 */
public class HelpPanel extends UiPart<Region> {
    /** Heading of each topic, keyed by the topic in lowercase. Existing general commands use the empty topic. */
    private final Map<String, Label> topicHeadings = new HashMap<>();

    @FXML
    private ScrollPane helpScrollPane;
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
                addTopicHeading(entry.getTopic());
                previousTopic = entry.getTopic();
            }
            addCommand(entry);
        }
        Label planned = new Label("Coming soon: tutorial groups, student IDs, attendance, participation and "
                + "assignments. Their domains are not available yet.");
        planned.setWrapText(true);
        planned.getStyleClass().add("coming-soon-note");
        commands.getChildren().add(planned);
    }

    private void addTopicHeading(String topic) {
        Label heading = new Label(HelpCatalog.getTopicHeading(topic));
        heading.getStyleClass().add("section-title");
        commands.getChildren().add(heading);
        topicHeadings.put(topic.toLowerCase(Locale.ROOT), heading);
    }

    /**
     * Scrolls the reference so that the commands of {@code topic} are at the top.
     * Scrolls to the top of the reference if {@code topic} is empty or has no commands.
     * The panel must be laid out first, so that the position of each heading is known.
     */
    public void scrollToTopic(String topic) {
        requireNonNull(topic);
        Label heading = topic.isEmpty() ? null : topicHeadings.get(topic.toLowerCase(Locale.ROOT));
        double scrollableHeight = commands.getHeight() - helpScrollPane.getViewportBounds().getHeight();
        double min = helpScrollPane.getVmin();
        if (heading == null || scrollableHeight <= 0) {
            helpScrollPane.setVvalue(min);
            return;
        }
        double fraction = Math.min(1, heading.getBoundsInParent().getMinY() / scrollableHeight);
        helpScrollPane.setVvalue(min + fraction * (helpScrollPane.getVmax() - min));
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
