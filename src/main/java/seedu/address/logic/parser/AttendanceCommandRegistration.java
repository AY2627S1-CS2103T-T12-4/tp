package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.AttendanceListCommand;
import seedu.address.logic.commands.AttendanceMarkCommand;
import seedu.address.logic.help.HelpEntry;

/**
 * Registers attendance commands and their offline help before application startup.
 */
public final class AttendanceCommandRegistration {
    private AttendanceCommandRegistration() {}

    /**
     * Adds attendance parsers and help to the given registry.
     *
     * @param registry The registry assembled before constructing the logic component.
     */
    public static void registerCommands(CommandRegistry registry) {
        requireNonNull(registry);
        registry.register("attendance", "mark", new AttendanceMarkCommandParser(),
                new HelpEntry("attendance", AttendanceMarkCommand.MESSAGE_USAGE,
                        "Mark a student in the active group present or absent for a week from 1 to 13. "
                                + "IDs and statuses ignore case. "
                                + "Marking the same student and week again replaces the earlier mark.",
                        List.of("attendance mark id/A0123456X w/3 s/present")));
        registry.register("attendance", "list", new AttendanceListCommandParser(),
                new HelpEntry("attendance", AttendanceListCommand.MESSAGE_USAGE,
                        "List the active group's attendance for a week from 1 to 13. Students without a mark show "
                                + "Not recorded. If no one has a mark that week, TAssist says so.",
                        List.of("attendance list w/3")));
    }
}
