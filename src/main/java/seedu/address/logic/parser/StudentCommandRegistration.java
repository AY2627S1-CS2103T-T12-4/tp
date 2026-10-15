package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.StudentAddCommand;
import seedu.address.logic.commands.StudentDeleteCommand;
import seedu.address.logic.commands.StudentListCommand;
import seedu.address.logic.help.HelpEntry;

/**
 * Registers student commands and their help entries before application startup.
 */
public final class StudentCommandRegistration {
    private StudentCommandRegistration() {}

    /**
     * Adds student parsers and help to the given registry.
     *
     * @param registry The registry assembled before constructing the logic component.
     */
    public static void registerCommands(CommandRegistry registry) {
        requireNonNull(registry);
        registry.register("student", "add", new StudentAddCommandParser(),
                new HelpEntry("student", StudentAddCommand.MESSAGE_USAGE,
                        "Add a student to the active group. Names: 1-60 letters, spaces, apostrophes or hyphens. "
                                + "IDs: 1-20 letters and digits, at least one digit; unique per group, ignoring case.",
                        List.of("student add n/Alice Tan id/A0123456X", "student add n/Ben Lim id/A0234567Y")));
        registry.register("student", "list", new StudentListCommandParser(),
                new HelpEntry("student", StudentListCommand.MESSAGE_USAGE,
                        "List the active group's students in roster order. Takes no parameters.",
                        List.of("student list")));
        registry.register("student", "delete", new StudentDeleteCommandParser(),
                new HelpEntry("student", StudentDeleteCommand.MESSAGE_USAGE,
                        "Remove a student and their records from the active group. IDs ignore case.",
                        List.of("student delete id/A0234567Y")));
    }
}
