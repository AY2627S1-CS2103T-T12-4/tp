package seedu.address.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class DataLoadStatusTest {

    @Test
    public void failed_reason_namesReasonBetweenGeneralMessages() {
        DataLoadStatus status = DataLoadStatus.failed("  Group T01 has more than one student with ID A1. ");

        assertTrue(status.isFailure());
        assertEquals("Saved data could not be read. Group T01 has more than one student with ID A1."
                + " No changes were made to the saved file.", status.getMessage());
    }

    @Test
    public void failed_blankReason_returnsGeneralFailure() {
        assertSame(DataLoadStatus.FAILED, DataLoadStatus.failed(" "));
        assertEquals("Saved data could not be read. No changes were made to the saved file.",
                DataLoadStatus.FAILED.getMessage());
        assertThrows(NullPointerException.class, () -> DataLoadStatus.failed(null));
    }

    @Test
    public void loadedAndMissing_areNotFailures() {
        assertFalse(DataLoadStatus.LOADED.isFailure());
        assertFalse(DataLoadStatus.MISSING.isFailure());
        assertEquals("No saved data found. Starting with an empty database.", DataLoadStatus.MISSING.getMessage());
    }

    @Test
    public void equals_comparesMessages() {
        DataLoadStatus status = DataLoadStatus.failed("Reason.");
        assertEquals(status, status);
        assertEquals(status, DataLoadStatus.failed("Reason."));
        assertEquals(status.hashCode(), DataLoadStatus.failed("Reason.").hashCode());
        assertNotEquals(status, DataLoadStatus.failed("Other reason."));
        assertNotEquals(status, DataLoadStatus.FAILED);
        assertNotEquals(DataLoadStatus.LOADED, DataLoadStatus.MISSING);
        assertNotEquals(status, null);
        assertNotEquals(status, status.getMessage());
    }
}
