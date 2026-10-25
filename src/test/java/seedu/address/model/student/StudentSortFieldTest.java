package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.StudentBuilder;

public class StudentSortFieldTest {
    @Test
    public void getComparator_name_ignoresCaseAndBreaksTiesById() {
        Student zoe = new StudentBuilder().withName("Zoe").withStudentId("A1").build();
        Student aliceHigh = new StudentBuilder().withName("alice Tan").withStudentId("C2").build();
        Student aliceLow = new StudentBuilder().withName("Alice Tan").withStudentId("B1").build();
        assertEquals(List.of(aliceLow, aliceHigh, zoe), List.of(zoe, aliceHigh, aliceLow).stream()
                .sorted(StudentSortField.NAME.getComparator()).toList());
        assertEquals(List.of(zoe, aliceLow, aliceHigh), List.of(zoe, aliceHigh, aliceLow).stream()
                .sorted(StudentSortField.ID.getComparator()).toList());
    }

    @Test
    public void getComparator_id_usesTextRatherThanNumericOrder() {
        Student a2 = new StudentBuilder().withStudentId("A2").build();
        Student a10 = new StudentBuilder().withStudentId("A10").build();
        assertEquals(List.of(a10, a2), List.of(a2, a10).stream()
                .sorted(StudentSortField.ID.getComparator()).toList());
    }
}
