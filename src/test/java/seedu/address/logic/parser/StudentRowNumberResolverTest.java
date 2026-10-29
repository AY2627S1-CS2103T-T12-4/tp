package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.StudentRowNumberResolver.replaceRowNumbers;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;
import static seedu.address.testutil.TypicalStudents.CARL;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.StudentSearchPredicate;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TypicalGroups;

public class StudentRowNumberResolverTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(TypicalGroups.NAME_T01);
    }

    @Test
    public void replaceRowNumbers_rowNumbers_replacedByStudentIds() {
        assertEquals("attendance mark id/" + ALICE.getStudentId() + " id/" + CARL.getStudentId() + " w/3 s/present",
                replaceRowNumbers("attendance mark id/1 id/3 w/3 s/present", model));
        assertEquals("student edit id/" + BEN.getStudentId() + " nid/2",
                replaceRowNumbers("  student edit id/02 nid/2 ", model));
    }

    @Test
    public void replaceRowNumbers_filteredTable_usesShownRows() {
        model.filterStudents(new StudentSearchPredicate("Carl", ""));

        assertEquals("student delete id/" + CARL.getStudentId(), replaceRowNumbers("student delete id/1", model));
    }

    @Test
    public void replaceRowNumbers_notARowNumber_unchanged() {
        String[] inputs = {"attendance clear id/0 w/3", "attendance clear id/4 w/3",
            "attendance clear id/A0123400X w/3", "attendance clear id/99999999999 w/3", "attendance clear id/1w/3",
            "student add n/Dan id/1", "STUDENT  find id/1"};
        for (String input : inputs) {
            assertEquals(input, replaceRowNumbers(input, model));
        }
    }

    @Test
    public void replaceRowNumbers_numberIsStudentId_keepsId() {
        model.addStudent(TypicalGroups.NAME_T01, new StudentBuilder().withName("Dan Koh").withStudentId("2").build());

        assertEquals("student delete id/2", replaceRowNumbers("student delete id/2", model));
    }

    @Test
    public void replaceRowNumbers_noActiveGroup_unchanged() {
        Model noGroup = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());

        assertEquals("student delete id/1", replaceRowNumbers("student delete id/1", noGroup));
    }

    @Test
    public void replaceRowNumbers_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> replaceRowNumbers(null, model));
        assertThrows(NullPointerException.class, () -> replaceRowNumbers("student list", null));
    }
}
