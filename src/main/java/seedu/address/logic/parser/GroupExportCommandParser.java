package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.logic.commands.GroupExportCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.storage.GroupRecordsCsvExporter;

/**
 * Parses a group export command, rejecting any parameters.
 */
public class GroupExportCommandParser implements Parser<GroupExportCommand> {
    private final GroupRecordsCsvExporter exporter;

    /**
     * Creates a parser whose commands export with {@code exporter}.
     */
    public GroupExportCommandParser(GroupRecordsCsvExporter exporter) {
        this.exporter = requireNonNull(exporter);
    }

    @Override
    public GroupExportCommand parse(String arguments) throws ParseException {
        new TAssistArgumentParser(List.of(), List.of()).parse(arguments);
        return new GroupExportCommand(exporter);
    }
}
