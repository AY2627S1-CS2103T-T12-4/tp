package seedu.address.logic.help;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Combines legacy command help with entries registered by TAssist features.
 */
public final class HelpCatalog {
    private static final List<HelpEntry> LEGACY_ENTRIES = List.of(
            new HelpEntry("", "view SCREEN",
                    "Open students, groups, attendance, participation, assignments, help, or storage.",
                    List.of("view attendance")),
            new HelpEntry("", "add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…",
                    "Add a contact to the roster.",
                    List.of("add n/Alex Tan p/91234567 e/alex@example.com a/12 Kent Ridge Rd")),
            new HelpEntry("", "list", "Show all contacts. Roster row numbers are the indices used by edit and delete.",
                    List.of("list")),
            new HelpEntry("", "find KEYWORD [MORE_KEYWORDS]…", "Find contacts by name, ignoring case.",
                    List.of("find Alex")),
            new HelpEntry("", "edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…",
                    "Edit a displayed contact.", List.of("edit 1 n/Alex Tan")),
            new HelpEntry("", "delete INDEX", "Delete the contact at the displayed row number.",
                    List.of("delete 1")),
            new HelpEntry("", "clear", "Delete every contact. This cannot be undone.", List.of("clear")),
            new HelpEntry("", "help", "Show this reference. F1 also opens Help; Escape focuses the command box.",
                    List.of("help")),
            new HelpEntry("", "exit", "Close TAssist.", List.of("exit")));

    private HelpCatalog() {
    }

    /**
     * Returns the legacy commands followed by commands registered by features.
     *
     * @param featureEntries The help entries registered with feature command parsers.
     * @return An immutable list of all help entries.
     */
    public static List<HelpEntry> getEntries(List<HelpEntry> featureEntries) {
        requireNonNull(featureEntries);
        List<HelpEntry> entries = new ArrayList<>(LEGACY_ENTRIES);
        entries.addAll(featureEntries);
        return List.copyOf(entries);
    }

    /**
     * Formats command entries as readable help text.
     *
     * @param entries The help entries to format.
     * @return A multi-line command reference.
     */
    public static String format(List<HelpEntry> entries) {
        requireNonNull(entries);
        StringBuilder output = new StringBuilder("Command reference");
        String previousTopic = null;
        for (HelpEntry entry : entries) {
            requireNonNull(entry);
            String topic = entry.getTopic();
            if (!topic.equals(previousTopic)) {
                output.append("\n\n").append(topic.isEmpty() ? "Current commands" : titleCase(topic) + " commands");
                previousTopic = topic;
            }
            output.append("\n").append(entry.getCommandFormat())
                    .append("\n  ").append(entry.getDescription());
            for (String example : entry.getExamples()) {
                output.append("\n  Example: ").append(example);
            }
        }
        return output.toString();
    }

    private static String titleCase(String topic) {
        return Character.toUpperCase(topic.charAt(0)) + topic.substring(1);
    }
}
