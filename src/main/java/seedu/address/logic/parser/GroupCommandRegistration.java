package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.util.List;

import seedu.address.commons.core.AppHome;
import seedu.address.logic.commands.GroupAddCommand;
import seedu.address.logic.commands.GroupDeleteCommand;
import seedu.address.logic.commands.GroupExportCommand;
import seedu.address.logic.commands.GroupListCommand;
import seedu.address.logic.commands.GroupRenameCommand;
import seedu.address.logic.commands.GroupUseCommand;
import seedu.address.logic.help.HelpEntry;
import seedu.address.storage.GroupRecordsCsvExporter;

/**
 * Registers tutorial-group commands and their help entries before application startup.
 */
public final class GroupCommandRegistration {
    /** Name of the folder, next to the TAssist JAR, that exports are written to by default. */
    public static final String EXPORT_FOLDER_NAME = "exports";

    private GroupCommandRegistration() {}

    /**
     * Adds tutorial-group parsers and help to the given registry. Exports are written to the
     * {@value #EXPORT_FOLDER_NAME} folder next to the TAssist JAR.
     *
     * @param registry The registry assembled before constructing the logic component.
     */
    public static void registerCommands(CommandRegistry registry) {
        registerCommands(registry, AppHome.getHomeDirectory().resolve(EXPORT_FOLDER_NAME));
    }

    /**
     * Adds tutorial-group parsers and help to the given registry, with exports written to {@code exportFolder}.
     *
     * @param registry The registry assembled before constructing the logic component.
     * @param exportFolder The folder that {@code group export} writes its files to.
     */
    public static void registerCommands(CommandRegistry registry, Path exportFolder) {
        requireNonNull(registry);
        requireNonNull(exportFolder);
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
        registry.register("group", "export", new GroupExportCommandParser(new GroupRecordsCsvExporter(exportFolder)),
                new HelpEntry("group", GroupExportCommand.MESSAGE_USAGE,
                        "Save the active group's attendance and participation for every week as a CSV file that "
                                + "opens in a spreadsheet. The file is in the exports folder next to TAssist and "
                                + "is replaced by the next export of the same group. Takes no parameters.",
                        List.of("group export")));
        registry.register("group", "rename", new GroupRenameCommandParser(),
                new HelpEntry("group", GroupRenameCommand.MESSAGE_USAGE,
                        "Rename a tutorial group. Its students and records stay with it.",
                        List.of("group rename g/T02 n/T02-Thu")));
        registry.register("group", "delete", new GroupDeleteCommandParser(),
                new HelpEntry("group", GroupDeleteCommand.MESSAGE_USAGE,
                        "Delete a tutorial group with all its students and records, after asking to confirm. "
                                + "Add f/yes to skip the prompt. Undo reverses it until TAssist is closed.",
                        List.of("group delete g/T02-Thu")));
    }
}
