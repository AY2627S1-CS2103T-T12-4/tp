package seedu.address.logic.help;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Combines legacy command help with entries registered by TAssist features.
 */
public final class HelpCatalog {
    /** Topics accepted by {@code help TOPIC}, in the order they are shown to the user. */
    public static final List<String> TOPICS = List.of("group", "student", "attendance", "participation",
            "assignment");

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
            new HelpEntry("", "help [TOPIC]",
                    "Show this reference, or only one topic. F1 also opens Help; Escape focuses the command box.",
                    List.of("help", "help student")),
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
     * Returns whether {@code topic} can be requested with {@code help TOPIC}. Matching ignores case.
     */
    public static boolean isValidTopic(String topic) {
        requireNonNull(topic);
        return TOPICS.contains(topic.toLowerCase(Locale.ROOT));
    }

    /**
     * Returns the entries whose topic is {@code topic}, ignoring case, in their original order.
     */
    public static List<HelpEntry> filterByTopic(List<HelpEntry> entries, String topic) {
        requireNonNull(entries);
        requireNonNull(topic);
        return entries.stream()
                .filter(entry -> entry.getTopic().equalsIgnoreCase(topic))
                .toList();
    }

    /**
     * Returns the heading shown above the commands of {@code topic}.
     * Existing general commands use the empty topic.
     */
    public static String getTopicHeading(String topic) {
        requireNonNull(topic);
        if (topic.isEmpty()) {
            return "Current commands";
        }
        return Character.toUpperCase(topic.charAt(0)) + topic.substring(1) + " commands";
    }
}
