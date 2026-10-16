package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.GroupAddCommand;
import seedu.address.logic.commands.GroupListCommand;
import seedu.address.logic.commands.GroupUseCommand;
import seedu.address.logic.help.HelpEntry;

/**
 * Registers tutorial-group commands and their help entries before application startup.
 */
public final class GroupCommandRegistration {
    private GroupCommandRegistration() {}

    /**
     * Adds tutorial-group parsers and help to the given registry.
     *
     * @param registry The registry assembled before constructing the logic component.
     */
    public static void registerCommands(CommandRegistry registry) {
        requireNonNull(registry);
        registry.register("group", "add", new GroupAddCommandParser(),
                new HelpEntry("group", GroupAddCommand.MESSAGE_USAGE,
                        "Create an empty tutorial group and make it active. Names have 1-60 letters, digits, "
                                + "spaces or hyphens, and ignore case and extra spaces.",
                        List.of("group add g/T01", "group add g/T02")));
        registry.register("group", "list", new GroupListCommandParser(),
                new HelpEntry("group", GroupListCommand.MESSAGE_USAGE,
                        "List tutorial groups in creation order and name the active group. Takes no parameters.",
                        List.of("group list")));
        registry.register("group", "use", new GroupUseCommandParser(),
                new HelpEntry("group", GroupUseCommand.MESSAGE_USAGE,
                        "Make an existing tutorial group active. Names ignore case. "
                                + "Each group keeps its own students and records.",
                        List.of("group use g/T01")));
    }
}
