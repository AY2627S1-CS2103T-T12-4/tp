package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAssignments.NAME_PS1;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AssignmentAddCommand;
import seedu.address.logic.commands.AssignmentDeleteCommand;
import seedu.address.logic.commands.AssignmentGradeCommand;
import seedu.address.logic.commands.AssignmentListCommand;
import seedu.address.logic.commands.AssignmentSubmitCommand;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.help.HelpEntry;
import seedu.address.model.ModelManager;

public class AssignmentCommandRegistrationTest {
    @Test
    public void registerCommands_dispatchesCommandsAndProvidesHelp() throws Exception {
        CommandRegistry registry = new CommandRegistry();
        AssignmentCommandRegistration.registerCommands(registry);
        TAssistParser parser = new TAssistParser(registry);

        assertEquals(new AssignmentAddCommand(NAME_PS1), parser.parseCommand(" assignment   add a/PS1 "));
        assertEquals(new AssignmentSubmitCommand(NAME_PS1, ALICE.getStudentId(), true),
                parser.parseCommand("assignment submit a/PS1 id/A0123456X s/yes"));
        assertEquals(new AssignmentGradeCommand(NAME_PS1, ALICE.getStudentId(), 87),
                parser.parseCommand("assignment grade a/PS1 id/A0123456X m/87"));
        assertEquals(new AssignmentListCommand(NAME_PS1), parser.parseCommand("assignment list a/ps1"));
        assertEquals(new AssignmentDeleteCommand(NAME_PS1, true), parser.parseCommand("assignment delete a/ps1 f/yes"));
        assertEquals(List.of(AssignmentAddCommand.MESSAGE_USAGE, AssignmentSubmitCommand.MESSAGE_USAGE,
                        AssignmentGradeCommand.MESSAGE_USAGE, AssignmentListCommand.MESSAGE_USAGE,
                        AssignmentDeleteCommand.MESSAGE_USAGE),
                parser.getHelpEntries().stream().filter(entry -> entry.getTopic().equals("assignment"))
                        .map(HelpEntry::getCommandFormat).toList());
        assertEquals(CommandResult.forHelp("Showing assignment commands.", "assignment"),
                parser.parseCommand("help assignment").execute(new ModelManager()));
    }

    @Test
    public void registerCommands_nullRegistry_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> AssignmentCommandRegistration.registerCommands(null));
    }
}
