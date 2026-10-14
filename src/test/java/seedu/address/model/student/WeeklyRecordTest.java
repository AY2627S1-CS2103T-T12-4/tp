package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class WeeklyRecordTest {
    @Test
    public void constructor_copiesRecordsAndExposesReadOnlyMap() {
        Map<Integer, String> source = new HashMap<>();
        source.put(1, "PRESENT");
        WeeklyRecord<String> records = new WeeklyRecord<>(source);

        source.put(2, "ABSENT");
        assertEquals(Map.of(1, "PRESENT"), records.getRecords());
        assertThrows(UnsupportedOperationException.class, () -> records.getRecords().put(2, "ABSENT"));
    }

    @Test
    public void withRecord_validWeeksReplacesWithoutChangingOriginal() {
        WeeklyRecord<String> empty = new WeeklyRecord<>();
        WeeklyRecord<String> first = empty.withRecord(1, "PRESENT");
        WeeklyRecord<String> last = first.withRecord(13, "ABSENT");
        WeeklyRecord<String> replaced = last.withRecord(1, "EXCUSED");

        assertTrue(empty.getRecords().isEmpty());
        assertEquals("PRESENT", first.getRecord(1).orElseThrow());
        assertEquals("ABSENT", last.getRecord(13).orElseThrow());
        assertEquals("EXCUSED", replaced.getRecord(1).orElseThrow());
        assertEquals(2, replaced.getRecords().size());
        assertTrue(replaced.getRecord(2).isEmpty());
    }

    @Test
    public void constructor_invalidData_throwsException() {
        assertThrows(NullPointerException.class, () -> new WeeklyRecord<>(null));
        assertThrows(IllegalArgumentException.class, () -> new WeeklyRecord<>(Map.of(0, "PRESENT")));
        assertThrows(IllegalArgumentException.class, () -> new WeeklyRecord<>(Map.of(14, "PRESENT")));

        Map<Integer, String> nullKey = new HashMap<>();
        nullKey.put(null, "PRESENT");
        assertThrows(IllegalArgumentException.class, () -> new WeeklyRecord<>(nullKey));

        Map<Integer, String> nullValue = new HashMap<>();
        nullValue.put(1, null);
        assertThrows(IllegalArgumentException.class, () -> new WeeklyRecord<>(nullValue));
    }

    @Test
    public void getRecordOrWithRecord_invalidWeek_throwsException() {
        WeeklyRecord<String> records = new WeeklyRecord<>();
        assertThrows(IllegalArgumentException.class, () -> records.getRecord(0));
        assertThrows(IllegalArgumentException.class, () -> records.getRecord(14));
        assertThrows(IllegalArgumentException.class, () -> records.withRecord(0, "PRESENT"));
        assertThrows(IllegalArgumentException.class, () -> records.withRecord(14, "PRESENT"));
        assertThrows(NullPointerException.class, () -> records.withRecord(1, null));
    }

    @Test
    public void equalsHashCodeAndToString() {
        WeeklyRecord<String> records = new WeeklyRecord<>(Map.of(1, "PRESENT"));
        WeeklyRecord<String> sameRecords = new WeeklyRecord<>(Map.of(1, "PRESENT"));
        WeeklyRecord<String> differentRecords = new WeeklyRecord<>(Map.of(1, "ABSENT"));

        assertEquals(records, records);
        assertEquals(records, sameRecords);
        assertEquals(records.hashCode(), sameRecords.hashCode());
        assertNotEquals(records, differentRecords);
        assertNotEquals(records, null);
        assertNotEquals(records, "records");
        assertTrue(records.toString().contains("1=PRESENT"));
        assertFalse(records.getRecord(2).isPresent());
    }
}
