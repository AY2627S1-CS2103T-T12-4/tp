package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;
import static seedu.address.testutil.TypicalStudents.CARL;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentSearchPredicate;
import seedu.address.testutil.StudentBuilder;
import seedu.address.testutil.TypicalGroups;

public class StudentDisplayTest {
    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());
        model.setActiveGroup(NAME_T01);
        model.removeStudent(NAME_T01, ALICE.getStudentId());
        model.addStudent(NAME_T01, ALICE);
    }

    @Test
    public void getDisplayedStudentList_rosterChanges_keepsLiveFilteredView() {
        ObservableList<Student> displayed = model.getDisplayedStudentList();
        model.filterStudents(new StudentSearchPredicate("a", ""));
        assertEquals(List.of(CARL, ALICE), displayed);

        Student aaron = new StudentBuilder().withName("Aaron").withStudentId("A1").build();
        model.addStudent(NAME_T01, aaron);
        assertEquals(List.of(CARL, ALICE, aaron), displayed);
        model.removeStudent(NAME_T01, CARL.getStudentId());
        Student updatedAlice = ALICE.withParticipationRecord(1, 5);
        model.setStudent(NAME_T01, updatedAlice);
        assertEquals(List.of(updatedAlice, aaron), displayed);

        model.filterStudents(new StudentSearchPredicate("Ben", ""));
        assertEquals(List.of(BEN), displayed);
        model.resetStudentDisplay();
        assertEquals(List.of(BEN, updatedAlice, aaron), displayed);
    }
}
