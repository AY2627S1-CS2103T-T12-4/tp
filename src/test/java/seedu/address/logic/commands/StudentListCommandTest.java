package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T03;
import static seedu.address.testutil.TypicalStudents.BEN;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.testutil.TypicalGroups;

public class StudentListCommandTest {
    @Test
    public void execute_populatedGroup_numbersStudentsInOrderWithoutChangingData() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T01);
        model.removeStudent(NAME_T01, BEN.getStudentId());
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        assertCommandSuccess(new StudentListCommand(), model,
                "Students in T01:\n1. Alice Tan | A0123456X\n2. Carl Goh | A0345678Z", expected);
    }

    @Test
    public void execute_emptyGroup_explainsHowToAddStudent() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T03);
        Model expected = new ModelManager(model.getTAssist(), new UserPrefs());
        assertCommandSuccess(new StudentListCommand(), model,
                "No students in T03. Add one using: student add n/NAME id/STUDENT_ID", expected);
    }

    @Test
    public void execute_noActiveGroup_reportsError() {
        assertCommandFailure(new StudentListCommand(), new ModelManager(), Messages.MESSAGE_NO_ACTIVE_GROUP);
    }

    @Test
    public void execute_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new StudentListCommand().execute(null));
    }
}
