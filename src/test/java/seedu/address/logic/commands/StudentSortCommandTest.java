package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.CARL;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.WorkspaceView;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.TAssist;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.StudentSearchPredicate;
import seedu.address.model.student.StudentSortField;
import seedu.address.testutil.TypicalGroups;

public class StudentSortCommandTest {
    @Test
    public void execute_filteredRoster_sortsMatchesAndListRestoresOriginalOrder() throws Exception {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
        model.removeStudent(TypicalGroups.NAME_T01, ALICE.getStudentId());
        model.addStudent(TypicalGroups.NAME_T01, ALICE);
        TAssist previousData = new TAssist(model.getTAssist());
        model.filterStudents(new StudentSearchPredicate("a", ""));

        CommandResult result = new StudentSortCommand(StudentSortField.NAME).execute(model);
        assertEquals(new CommandResult("Sorted 2 student(s) in T01 by name.", WorkspaceView.STUDENTS), result);
        assertEquals(List.of(ALICE, CARL), model.getDisplayedStudentList());
        assertEquals(previousData, model.getTAssist());

        new StudentListCommand().execute(model);
        assertEquals(model.getActiveGroupStudentList(), model.getDisplayedStudentList());
        assertEquals(ALICE, model.getDisplayedStudentList().getLast());
    }

    @Test
    public void execute_noMatches_explainsHowToRestoreRoster() throws Exception {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
        model.filterStudents(new StudentSearchPredicate("nobody", ""));
        assertEquals(new CommandResult("No matching students in T01. Use student list to show all students.",
                        WorkspaceView.STUDENTS),
                new StudentSortCommand(StudentSortField.ID).execute(model));
    }

    @Test
    public void execute_noActiveGroup_reportsError() {
        assertCommandFailure(new StudentSortCommand(StudentSortField.ID), new ModelManager(),
                Messages.MESSAGE_NO_ACTIVE_GROUP);
    }
}
