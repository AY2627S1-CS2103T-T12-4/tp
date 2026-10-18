package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.AssignmentAddCommand;
import seedu.address.logic.commands.AssignmentListCommand;
import seedu.address.logic.help.HelpEntry;

/**
 * Registers assignment commands and their help entries before application startup.
 */
public final class AssignmentCommandRegistration {
    private AssignmentCommandRegistration() {}

    /**
     * Adds assignment parsers and help to the given registry.
     *
     * @param registry The registry assembled before constructing the logic component.
     */
    public static void registerCommands(CommandRegistry registry) {
        requireNonNull(registry);
        registry.register("assignment", "add", new AssignmentAddCommandParser(),
                new HelpEntry("assignment", AssignmentAddCommand.MESSAGE_USAGE,
                        "Create an assignment in the active group. Names: 1-60 letters, digits, spaces or hyphens; "
                                + "unique per group, ignoring case.",
                        List.of("assignment add a/PS1")));
        registry.register("assignment", "list", new AssignmentListCommandParser(),
                new HelpEntry("assignment", AssignmentListCommand.MESSAGE_USAGE,
                        "List every student's submission status and grade for an assignment of the active group. "
                                + "Names ignore case.",
                        List.of("assignment list a/PS1")));
    }
}
