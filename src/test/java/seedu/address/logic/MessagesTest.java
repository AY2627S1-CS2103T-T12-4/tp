package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.Prefix;

public class MessagesTest {
    @Test
    public void getErrorMessageForDuplicatePrefixes_onePrefix_namesPrefixAndHowToFix() {
        assertEquals("Repeated parameter(s): g/. Include each only once.",
                Messages.getErrorMessageForDuplicatePrefixes(new Prefix("g/")));
    }
}
