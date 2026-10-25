package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.GroupExportCommand;
import seedu.address.storage.GroupRecordsCsvExporter;

public class GroupExportCommandParserTest {
    private final GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(Path.of("exports"));
    private final GroupExportCommandParser parser = new GroupExportCommandParser(exporter);

    @Test
    public void parse_noArguments_returnsExportCommand() throws Exception {
        assertEquals(new GroupExportCommand(exporter), parser.parse(""));
        assertEquals(new GroupExportCommand(exporter), parser.parse(" \t "));
    }

    @Test
    public void parse_extraInput_reportsNoParameters() {
        for (String input : new String[] {"T01", "g/T01", "records.csv", "x/value"}) {
            assertParseFailure(parser, input, TAssistArgumentParser.MESSAGE_NO_PARAMETERS);
        }
    }

    @Test
    public void constructor_nullExporter_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupExportCommandParser(null));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }
}
