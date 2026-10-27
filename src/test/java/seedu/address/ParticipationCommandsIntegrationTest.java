package seedu.address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.TAssist;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class ParticipationCommandsIntegrationTest extends AppIntegrationTest {
    @BeforeEach
    public void setUp() throws Exception {
        app = openAppWith(new TAssistBuilder(TypicalGroups.getTypicalTAssist()).withActiveGroup(NAME_T01).build());
    }

    @Test
    public void execute_setUpdateAndRestart_preservesScores() throws Exception {
        assertEquals("Recorded participation score 2 for Alice Tan in Week 3.",
                feedback("participation set id/a0123456x w/3 s/2"));
        restart();
        assertEquals("Participation for T01, Week 3:\nAlice Tan | A0123456X | 2"
                + "\nBen Lim | A0234567Y | Not recorded\nCarl Goh | A0345678Z | Not recorded",
                feedback("participation list w/3"));
        assertEquals("Updated Alice Tan's participation score for Week 3: 2 to 4.",
                feedback("participation set id/A0123456X w/3 s/4"));
        feedback("participation set id/A0123456X w/13 s/0");
        TAssist expected = new TAssist(app.model.getTAssist());
        restart();
        assertEquals(expected, app.model.getTAssist());
        assertEquals(4, app.model.getActiveGroupStudentList().getFirst().getParticipationRecord(3).orElseThrow());
        assertEquals(0, app.model.getActiveGroupStudentList().getFirst().getParticipationRecord(13).orElseThrow());
    }

    @Test
    public void execute_rejectedInput_preservesScoresAndSavedFile() throws Exception {
        feedback("participation set id/A0123456X w/3 s/2");
        for (String command : List.of("participation set", "participation set id/A0123456X w/3",
                "participation set id/ w/3 s/4", "participation set id/A0123456X w/0 s/4",
                "participation set id/A0123456X w/3 s/-1", "participation set id/A0123456X w/3 s/6",
                "participation set id/A0123456X w/3 s/2.5", "participation set id/A0123456X w/3 s/" + "9".repeat(10000),
                "participation set id/A0123456X w/3 s/4 s/5", "participation set id/A0123456X w/3 x/4",
                "participation set id/A0123456X w/3s/4", "participation list", "participation list w/14",
                "participation list w/3 w/4", "participation list w/3 s/4", "participation", "participation mark")) {
            assertRejected(command, ParseException.class);
        }
        assertRejected("participation set id/A999 w/3 s/4", CommandException.class);
    }

    @Test
    public void execute_noActiveGroup_preservesData() throws Exception {
        app.model.setTAssist(TypicalGroups.getTypicalTAssist());
        app.storage.saveTAssist(app.model.getTAssist());
        assertRejected("participation list w/3", CommandException.class);
        assertRejected("participation set id/A0123456X w/3 s/2", CommandException.class);
    }

    @Test
    public void init_participationHelp_registersWorkingExamples() throws Exception {
        List<HelpEntry> entries = HelpCatalog.filterByTopic(app.logic.getHelpEntries(), "participation");
        assertEquals(List.of("participation set id/STUDENT_ID w/WEEK s/SCORE", "participation list w/WEEK"),
                entries.stream().map(HelpEntry::getCommandFormat).toList());
        assertEquals(CommandResult.forHelp("Showing participation commands.", "participation"),
                app.logic.execute("help participation"));
        for (HelpEntry entry : entries) {
            for (String example : entry.getExamples()) {
                app.logic.execute(example);
            }
        }
        assertEquals(2, app.model.getGroup(NAME_T01).findStudent(ALICE.getStudentId()).orElseThrow()
                .getParticipationRecord(3).orElseThrow());
    }
}
