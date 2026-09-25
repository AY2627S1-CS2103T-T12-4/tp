package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests the optional remark value object. */
public class RemarkTest {

    @Test
    public void constructor_emptyValueAllowed() {
        assertEquals("", new Remark("").value);
    }

    @Test
    public void equals_sameValue() {
        assertEquals(new Remark("Likes baseball"), new Remark("Likes baseball"));
        assertNotEquals(new Remark("Likes baseball"), new Remark("Plays tennis"));
    }

    @Test
    public void constructor_nullValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Remark(null));
    }
}
