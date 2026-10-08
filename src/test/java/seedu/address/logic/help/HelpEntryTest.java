package seedu.address.logic.help;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.HelpEntryBuilder;

public class HelpEntryTest {
    @Test
    public void constructor_null_throwsNullPointerException() {
        List<String> examples = List.of("student list");
        assertThrows(NullPointerException.class, () -> new HelpEntry(null, "student list", "Description", examples));
        assertThrows(NullPointerException.class, () -> new HelpEntry("student", null, "Description", examples));
        assertThrows(NullPointerException.class, () -> new HelpEntry("student", "student list", null, examples));
        assertThrows(NullPointerException.class, () -> new HelpEntry("student", "student list", "Description", null));
    }

    @Test
    public void constructor_invalidContent_throwsIllegalArgumentException() {
        List<String> examples = List.of("student list");
        assertThrows(IllegalArgumentException.class, () -> new HelpEntry("student", " ", "Description", examples));
        assertThrows(IllegalArgumentException.class, () -> new HelpEntry("student", "student list", " ", examples));
        assertThrows(IllegalArgumentException.class, () ->
                new HelpEntry("student", "student list", "Description", List.of()));
        assertThrows(IllegalArgumentException.class, () ->
                new HelpEntry("student", "student list", "Description", List.of("student list", " ")));
    }

    @Test
    public void constructor_emptyTopic_success() {
        assertEquals("", new HelpEntryBuilder().withTopic("").build().getTopic());
    }

    @Test
    public void getExamples_isImmutableCopyOfInput() {
        List<String> examples = new ArrayList<>(List.of("student list"));
        HelpEntry entry = new HelpEntry("student", "student list", "Description", examples);
        examples.add("student add");

        assertEquals(List.of("student list"), entry.getExamples());
        assertThrows(UnsupportedOperationException.class, () -> entry.getExamples().clear());
    }

    @Test
    public void equals() {
        HelpEntry entry = new HelpEntryBuilder().build();

        assertTrue(entry.equals(entry));
        assertTrue(entry.equals(new HelpEntryBuilder().build()));
        assertEquals(entry.hashCode(), new HelpEntryBuilder().build().hashCode());
        assertFalse(entry.equals(null));
        assertFalse(entry.equals("student list"));
        assertNotEquals(entry, new HelpEntryBuilder().withTopic("group").build());
        assertNotEquals(entry, new HelpEntryBuilder().withCommandFormat("student add").build());
        assertNotEquals(entry, new HelpEntryBuilder().withDescription("Other").build());
        assertNotEquals(entry, new HelpEntryBuilder().withExamples("student list", "student list x").build());
    }

    @Test
    public void toString_containsAllFields() {
        String text = new HelpEntryBuilder().build().toString();

        assertTrue(text.contains("topic=student"));
        assertTrue(text.contains("commandFormat=student list"));
        assertTrue(text.contains("description=Description"));
        assertTrue(text.contains("examples=[student list]"));
    }
}
