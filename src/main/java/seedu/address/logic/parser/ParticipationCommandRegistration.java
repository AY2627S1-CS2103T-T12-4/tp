package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.ParticipationListCommand;
import seedu.address.logic.commands.ParticipationSetCommand;
import seedu.address.logic.help.HelpEntry;

/**
 * Registers participation commands and their help entries before application startup.
 */
public final class ParticipationCommandRegistration {
    private ParticipationCommandRegistration() {}

    /**
     * Adds participation parsers and help to the given registry.
     *
     * @param registry The registry assembled before constructing the logic component.
     */
    public static void registerCommands(CommandRegistry registry) {
        requireNonNull(registry);
        registry.register("participation", "list", new ParticipationListCommandParser(),
                new HelpEntry("participation", ParticipationListCommand.MESSAGE_USAGE,
                        "List the active group's scores for a week from 1 to 13. Missing scores show Not recorded.",
                        List.of("participation list w/3")));
        registry.register("participation", "set", new ParticipationSetCommandParser(),
                new HelpEntry("participation", ParticipationSetCommand.MESSAGE_USAGE,
                        "Set a whole-number score from 0 to 5 for a week from 1 to 13 in the active group. "
                                + "IDs ignore case. Setting the same week again replaces its score.",
                        List.of("participation set id/A0123456X w/3 s/2")));
    }
}
