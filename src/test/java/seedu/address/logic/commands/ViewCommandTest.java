package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.WorkspaceView;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameContainsKeywordsPredicate;

public class ViewCommandTest {
    @Test
    public void execute_filteredRoster_preservesDataAndFilter() {
        Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        Model expected = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        NameContainsKeywordsPredicate predicate = new NameContainsKeywordsPredicate(List.of("Alice"));
        model.updateFilteredPersonList(predicate);
        expected.updateFilteredPersonList(predicate);
        CommandResult result = new ViewCommand(WorkspaceView.GROUPS).execute(model);
        assertEquals(expected, model);
        assertEquals(WorkspaceView.GROUPS, result.getView().orElseThrow());
        assertFalse(result.isExit());
        assertFalse(result.isShowHelp());
    }

    @Test
    public void constructor_nullScreen_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ViewCommand(null));
    }
}
