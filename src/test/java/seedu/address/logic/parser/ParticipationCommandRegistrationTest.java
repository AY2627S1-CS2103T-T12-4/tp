package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.ParticipationClearCommand;
import seedu.address.logic.commands.ParticipationListCommand;
import seedu.address.logic.commands.ParticipationSetCommand;
import seedu.address.logic.help.HelpEntry;
import seedu.address.model.ModelManager;

public class ParticipationCommandRegistrationTest {
    @Test
    public void registerCommands_dispatchesEveryCommandAndProvidesHelp() throws Exception {
        CommandRegistry registry = new CommandRegistry();
        ParticipationCommandRegistration.registerCommands(registry);
        TAssistParser parser = new TAssistParser(registry);

        assertEquals(new ParticipationSetCommand(ALICE.getStudentId(), 3, 2),
                parser.parseCommand(" participation   set id/A0123456X w/3 s/2 "));
        assertEquals(new ParticipationListCommand(3), parser.parseCommand("participation list w/3"));
        assertEquals(new ParticipationClearCommand(ALICE.getStudentId(), 3),
                parser.parseCommand("participation clear id/a0123456x w/3"));
        assertEquals(List.of(ParticipationSetCommand.MESSAGE_USAGE, ParticipationListCommand.MESSAGE_USAGE,
                        ParticipationClearCommand.MESSAGE_USAGE),
                parser.getHelpEntries().stream().filter(entry -> entry.getTopic().equals("participation"))
                        .map(HelpEntry::getCommandFormat).toList());
        assertEquals(CommandResult.forHelp("Showing participation commands.", "participation"),
                parser.parseCommand("help participation").execute(new ModelManager()));
    }

    @Test
    public void registerCommands_nullRegistry_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParticipationCommandRegistration.registerCommands(null));
    }
}
