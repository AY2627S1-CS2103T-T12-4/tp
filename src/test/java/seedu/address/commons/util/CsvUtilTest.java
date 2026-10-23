package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class CsvUtilTest {

    @Test
    public void formatField_plainText_unchanged() {
        assertEquals("Alice Tan", CsvUtil.formatField("Alice Tan"));
        assertEquals("Chloe O'Neil-Ng", CsvUtil.formatField("Chloe O'Neil-Ng"));
        assertEquals("", CsvUtil.formatField(""));
    }

    @Test
    public void formatField_separatorQuoteOrLineBreak_quotedWithQuotesDoubled() {
        assertEquals("\"Tan, Alice\"", CsvUtil.formatField("Tan, Alice"));
        assertEquals("\"The \"\"T01\"\" group\"", CsvUtil.formatField("The \"T01\" group"));
        assertEquals("\"Week 1\nWeek 2\"", CsvUtil.formatField("Week 1\nWeek 2"));
        assertEquals("\" padded \"", CsvUtil.formatField(" padded "));
    }

    @Test
    public void formatField_formulaStart_shownAsText() {
        assertEquals("'=SUM(A1:A3)", CsvUtil.formatField("=SUM(A1:A3)"));
        assertEquals("'+65", CsvUtil.formatField("+65"));
        assertEquals("'-Ann", CsvUtil.formatField("-Ann"));
        assertEquals("'@cmd", CsvUtil.formatField("@cmd"));
        assertEquals("\"'=1,2\"", CsvUtil.formatField("=1,2"));
    }

    @Test
    public void formatRow_fields_joinedByCommas() {
        assertEquals("Alice Tan,A0123456X,Present,,3",
                CsvUtil.formatRow(List.of("Alice Tan", "A0123456X", "Present", "", "3")));
        assertEquals("", CsvUtil.formatRow(List.of()));
    }

    @Test
    public void formatTable_rows_eachEndedByCrlf() {
        assertEquals("Name,Week 1\r\nAlice Tan,3\r\n",
                CsvUtil.formatTable(List.of(List.of("Name", "Week 1"), List.of("Alice Tan", "3"))));
        assertEquals("", CsvUtil.formatTable(List.of()));
    }

    @Test
    public void format_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> CsvUtil.formatTable(null));
        assertThrows(NullPointerException.class, () -> CsvUtil.formatRow(null));
        assertThrows(NullPointerException.class, () -> CsvUtil.formatRow(Arrays.asList("a", null)));
        assertThrows(NullPointerException.class, () -> CsvUtil.formatField(null));
    }
}
