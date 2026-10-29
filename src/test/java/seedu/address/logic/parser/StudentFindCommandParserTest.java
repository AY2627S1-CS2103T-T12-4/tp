package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.student.Student;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TypicalGroups;

public class StudentFindCommandParserTest {
    private final StudentFindCommandParser parser = new StudentFindCommandParser();

    @Test
    public void parse_nameParticles_preservesFragmentsInEitherParameterOrder() throws Exception {
        for (String name : List.of("Ravi s/o Kumar", "Siti d/o Ahmad", "Muthu a/l Raju", "Nur A/P Hassan")) {
            Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
            model.setActiveGroup(TypicalGroups.NAME_T01);
            Student student = new StudentBuilder().withName(name).withStudentId("A1").build();
            model.addStudent(TypicalGroups.NAME_T01, student);
            String fragment = name.substring(0, name.lastIndexOf(' '));
            for (String input : List.of(" n/" + fragment + " id/A1", " id/A1 n/" + fragment,
                    " n/" + name)) {
                parser.parse(input).execute(model);
                assertEquals(List.of(student), model.getDisplayedStudentList());
            }
        }
        assertThrows(ParseException.class, () -> parser.parse(" n/Ravi x/o Kumar"));
        assertThrows(ParseException.class, () -> parser.parse(" n/Ravi s/o w/1"));
    }

    @Test
    public void parse_optionalFiltersInEitherOrder_matchesFragments() throws Exception {
        for (String input : List.of(" n/liCE", " id/0123", " n/ce ta id/x", " id/x n/ce ta")) {
            Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
            model.setActiveGroup(TypicalGroups.NAME_T01);
            parser.parse(input).execute(model);
            assertEquals(List.of(ALICE), model.getDisplayedStudentList());
        }
    }

    @Test
    public void parse_invalidFilters_rejectsInput() {
        for (String input : List.of("", " ", " n/", " id/ ", " n/Alice id/", " n/ id/A1",
                " n/Alice n/Ben", " id/A1 id/A2", " x/Alice", " Alice", " n/Alice w/1")) {
            assertThrows(ParseException.class, () -> parser.parse(input));
        }
        assertThrows(NullPointerException.class, () -> parser.parse(null));
        assertThrows(ParseException.class, StudentFindCommandParser.MESSAGE_MISSING_FILTER, () -> parser.parse(""));
        String blankMessage = String.format(StudentFindCommandParser.MESSAGE_BLANK_FILTER, "n/");
        assertThrows(ParseException.class, blankMessage, () -> parser.parse(" n/"));
    }
}
