package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AttendanceListCommand;
import seedu.address.logic.commands.AttendanceMarkCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.help.HelpEntry;
import seedu.address.model.ModelManager;
import seedu.address.model.student.AttendanceStatus;

public class AttendanceCommandRegistrationTest {
    @Test
    public void registerCommands_dispatchesBothCommandsAndProvidesHelp() throws Exception {
        CommandRegistry registry = new CommandRegistry();
        AttendanceCommandRegistration.registerCommands(registry);
        TAssistParser parser = new TAssistParser(registry);

        assertEquals(new AttendanceMarkCommand(ALICE.getStudentId(), 3, AttendanceStatus.PRESENT),
                parser.parseCommand(" attendance   mark id/A0123456X w/3 s/present "));
        assertEquals(new AttendanceListCommand(3), parser.parseCommand("attendance list w/3"));
        assertEquals(List.of(AttendanceMarkCommand.MESSAGE_USAGE, AttendanceListCommand.MESSAGE_USAGE),
                parser.getHelpEntries().stream().filter(entry -> entry.getTopic().equals("attendance"))
                        .map(HelpEntry::getCommandFormat).toList());
        assertEquals(CommandResult.forHelp("Showing attendance commands.", "attendance"),
                parser.parseCommand("help attendance").execute(new ModelManager()));
    }

    @Test
    public void registerCommands_nullRegistry_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> AttendanceCommandRegistration.registerCommands(null));
    }
}
