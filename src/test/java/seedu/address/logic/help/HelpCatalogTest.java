package seedu.address.logic.help;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.HelpEntryBuilder;

/** Tests how legacy and feature command help is combined, filtered and formatted. */
public class HelpCatalogTest {
    @Test
    public void getEntries_appendsFeatureEntriesAndReturnsImmutableList() {
        HelpEntry featureEntry = new HelpEntryBuilder().build();
        int legacyCount = HelpCatalog.getEntries(List.of()).size();

        List<HelpEntry> entries = HelpCatalog.getEntries(List.of(featureEntry));

        assertEquals(legacyCount + 1, entries.size());
        assertEquals("", entries.getFirst().getTopic());
        assertEquals(featureEntry, entries.getLast());
        assertThrows(UnsupportedOperationException.class, entries::clear);
    }

    @Test
    public void getEntries_legacyEntriesHaveNoTopicAndCoverEveryCommandWord() {
        List<HelpEntry> entries = HelpCatalog.getEntries(List.of());

        assertTrue(entries.stream().allMatch(entry -> entry.getTopic().isEmpty()));
        for (String commandWord : List.of("view", "add", "list", "find", "edit", "delete", "clear", "help", "exit")) {
            assertTrue(entries.stream().anyMatch(entry -> entry.getCommandFormat().startsWith(commandWord)),
                    "Missing help for " + commandWord);
        }
    }

    @Test
    public void getEntries_nullThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> HelpCatalog.getEntries(null));
    }

    @Test
    public void isValidTopic() {
        for (String topic : HelpCatalog.TOPICS) {
            assertTrue(HelpCatalog.isValidTopic(topic));
        }
        assertTrue(HelpCatalog.isValidTopic("STUDENT"));

        assertFalse(HelpCatalog.isValidTopic(""));
        assertFalse(HelpCatalog.isValidTopic("assignments"));
        assertFalse(HelpCatalog.isValidTopic("student list"));
        assertThrows(NullPointerException.class, () -> HelpCatalog.isValidTopic(null));
    }

    @Test
    public void filterByTopic_keepsOnlyMatchingEntriesInOrder() {
        HelpEntry studentList = new HelpEntryBuilder().withCommandFormat("student list").build();
        HelpEntry studentAdd = new HelpEntryBuilder().withCommandFormat("student add").build();
        HelpEntry groupAdd = new HelpEntryBuilder().withTopic("group").withCommandFormat("group add").build();
        List<HelpEntry> entries = List.of(studentList, groupAdd, studentAdd);

        assertEquals(List.of(studentList, studentAdd), HelpCatalog.filterByTopic(entries, "student"));
        assertEquals(List.of(studentList, studentAdd), HelpCatalog.filterByTopic(entries, "STUDENT"));
        assertTrue(HelpCatalog.filterByTopic(entries, "attendance").isEmpty());
        assertThrows(NullPointerException.class, () -> HelpCatalog.filterByTopic(null, "student"));
        assertThrows(NullPointerException.class, () -> HelpCatalog.filterByTopic(entries, null));
    }

    @Test
    public void getTopicHeading() {
        assertEquals("Current commands", HelpCatalog.getTopicHeading(""));
        assertEquals("Student commands", HelpCatalog.getTopicHeading("student"));
        assertThrows(NullPointerException.class, () -> HelpCatalog.getTopicHeading(null));
    }
}
