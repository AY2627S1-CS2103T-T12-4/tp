package seedu.address.logic.help;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests how legacy and feature command help is combined and formatted. */
public class HelpCatalogTest {
    @Test
    public void getEntries_appendsFeatureEntriesAndReturnsImmutableList() {
        HelpEntry featureEntry = helpEntry("student", "student list");

        List<HelpEntry> entries = HelpCatalog.getEntries(List.of(featureEntry));

        assertEquals(10, entries.size());
        assertEquals("", entries.getFirst().getTopic());
        assertEquals(featureEntry, entries.getLast());
        assertThrows(UnsupportedOperationException.class, entries::clear);
    }

    @Test
    public void format_groupsLegacyAndFeatureCommandsWithExamples() {
        HelpEntry featureEntry = helpEntry("student", "student list");

        String formatted = HelpCatalog.format(List.of(helpEntry("", "help"), featureEntry));

        assertTrue(formatted.contains("Current commands\nhelp"));
        assertTrue(formatted.contains("Student commands\nstudent list"));
        assertTrue(formatted.contains("Example: student list"));
    }

    @Test
    public void format_emptyEntriesReturnsHeading() {
        assertEquals("Command reference", HelpCatalog.format(List.of()));
    }

    @Test
    public void getEntries_nullThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> HelpCatalog.getEntries(null));
    }

    private HelpEntry helpEntry(String topic, String commandFormat) {
        return new HelpEntry(topic, commandFormat, "Description", List.of(commandFormat));
    }
}
