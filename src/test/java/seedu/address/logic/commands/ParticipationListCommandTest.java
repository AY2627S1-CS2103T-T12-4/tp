package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T02;
import static seedu.address.testutil.TypicalGroups.NAME_T03;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.DANIEL;
import static seedu.address.testutil.TypicalStudents.ELLE;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.TypicalGroups;

public class ParticipationListCommandTest {
    @Test
    public void execute_mixedRecords_distinguishesZeroFromMissingWithoutChangingData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T01);
        model.setStudent(NAME_T01, ALICE.withParticipationRecord(3, 0));
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        assertCommandSuccess(new ParticipationListCommand(3), model,
                "Participation for T01, Week 3:\n1 recorded, 2 not recorded.\nNot recorded: Ben Lim, Carl Goh."
                        + "\nAlice Tan | A0123456X | 0"
                        + "\nBen Lim | A0234567Y | Not recorded\nCarl Goh | A0345678Z | Not recorded", expected);
        assertCommandSuccess(new ParticipationListCommand(13), model,
                "Participation for T01, Week 13:\n3 not recorded.\nNot recorded: Alice Tan, Ben Lim, Carl Goh."
                        + "\nAlice Tan | A0123456X | Not recorded"
                        + "\nBen Lim | A0234567Y | Not recorded\nCarl Goh | A0345678Z | Not recorded", expected);
    }

    @Test
    public void execute_everyoneScored_omitsNotRecordedCountAndLine() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T02);
        model.setStudent(NAME_T02, DANIEL.withParticipationRecord(3, 5));
        model.setStudent(NAME_T02, ELLE.withParticipationRecord(3, 0));
        assertCommandSuccess(new ParticipationListCommand(3), model,
                "Participation for T02, Week 3:\n2 recorded.\nDaniel Ong | A0456789W | 5\nElle Teo | A0567890U | 0",
                new ModelManager(model.getTAssist(), new UserPrefs()));
    }

    @Test
    public void execute_emptyGroup_reportsNoStudents() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T03);
        assertCommandSuccess(new ParticipationListCommand(1), model, "No students in T03.",
                new ModelManager(model.getTAssist(), new UserPrefs()));
    }

    @Test
    public void execute_noActiveGroup_reportsError() {
        assertCommandFailure(new ParticipationListCommand(3), new ModelManager(), Messages.MESSAGE_NO_ACTIVE_GROUP);
    }

    @Test
    public void invalidInputs_throwException() {
        assertThrows(IllegalArgumentException.class, () -> new ParticipationListCommand(0));
        assertThrows(IllegalArgumentException.class, () -> new ParticipationListCommand(14));
        assertThrows(NullPointerException.class, () -> new ParticipationListCommand(3).execute(null));
    }

    @Test
    public void equals_comparesWeek() {
        ParticipationListCommand command = new ParticipationListCommand(3);
        assertEquals(command, command);
        assertEquals(command, new ParticipationListCommand(3));
        assertEquals(command.hashCode(), new ParticipationListCommand(3).hashCode());
        assertNotEquals(command, new ParticipationListCommand(4));
        assertNotEquals(command, null);
        assertNotEquals(command, "participation");
    }
}
