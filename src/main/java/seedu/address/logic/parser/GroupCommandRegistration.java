package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.GroupAddCommand;
import seedu.address.logic.commands.GroupDeleteCommand;
import seedu.address.logic.commands.GroupListCommand;
import seedu.address.logic.commands.GroupRenameCommand;
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
                        "Create an empty tutorial group and make it active. Names ignore case and extra spaces.",
                        List.of("group add g/T01", "group add g/Thursday 1")));
        registry.register("group", "list", new GroupListCommandParser(),
                new HelpEntry("group", GroupListCommand.MESSAGE_USAGE,
                        "List tutorial groups in creation order and show the active group.", List.of("group list")));
        registry.register("group", "delete", new GroupDeleteCommandParser(),
                new HelpEntry("group", GroupDeleteCommand.MESSAGE_USAGE,
                        "Delete a tutorial group with all its students and records. This cannot be undone.",
                        List.of("group delete g/T01")));
        registry.register("group", "rename", new GroupRenameCommandParser(),
                new HelpEntry("group", GroupRenameCommand.MESSAGE_USAGE,
                        "Rename a tutorial group. Its students and records stay with it.",
                        List.of("group rename g/T01 n/T01-Thu")));
        registry.register("group", "use", new GroupUseCommandParser(),
                new HelpEntry("group", GroupUseCommand.MESSAGE_USAGE,
                        "Switch to an existing tutorial group. Its students and records stay separate.",
                        List.of("group use g/T02")));
    }
}
