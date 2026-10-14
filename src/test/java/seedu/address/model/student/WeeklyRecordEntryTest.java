package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.TypicalStudents;

public class WeeklyRecordEntryTest {
    @Test
    public void formatValueOrNotRecorded_recordAndMissingRecord_formatsCorrectly() {
        WeeklyRecordEntry<Integer> recorded = new WeeklyRecordEntry<>(TypicalStudents.ALICE, Optional.of(4));
        WeeklyRecordEntry<Integer> missing = new WeeklyRecordEntry<>(TypicalStudents.BEN, Optional.empty());

        assertEquals(TypicalStudents.ALICE, recorded.getStudent());
        assertEquals(Optional.of(4), recorded.getRecord());
        assertEquals("4 points", recorded.formatValueOrNotRecorded(value -> value + " points"));
        assertEquals("Not recorded", missing.formatValueOrNotRecorded(Object::toString));
    }

    @Test
    public void constructorOrFormatter_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new WeeklyRecordEntry<>(null, Optional.empty()));
        assertThrows(NullPointerException.class, () -> new WeeklyRecordEntry<>(TypicalStudents.ALICE, null));

        WeeklyRecordEntry<Integer> recorded = new WeeklyRecordEntry<>(TypicalStudents.ALICE, Optional.of(4));
        assertThrows(NullPointerException.class, () -> recorded.formatValueOrNotRecorded(null));
        assertThrows(NullPointerException.class, () -> recorded.formatValueOrNotRecorded(value -> null));
    }
}
