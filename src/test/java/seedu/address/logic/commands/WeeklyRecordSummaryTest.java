package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;
import static seedu.address.testutil.TypicalStudents.CARL;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

import seedu.address.model.student.WeeklyRecordEntry;

public class WeeklyRecordSummaryTest {
    private static final Map<String, Predicate<Integer>> PARITY_CATEGORIES = createParityCategories();

    @Test
    public void summarize_everyCategoryAndMissingRecord_countsInCategoryOrderAndNamesMissingStudents() {
        assertEquals("1 even, 1 odd, 1 not recorded.\nNot recorded: Carl Goh.",
                summarize(Optional.of(2), Optional.of(3), Optional.empty()));
    }

    @Test
    public void summarize_everyoneRecorded_omitsZeroCountsAndNamesLine() {
        assertEquals("3 even.", summarize(Optional.of(0), Optional.of(2), Optional.of(4)));
    }

    @Test
    public void summarize_nobodyRecorded_countsAndNamesEveryone() {
        assertEquals("3 not recorded.\nNot recorded: Alice Tan, Ben Lim, Carl Goh.",
                summarize(Optional.empty(), Optional.empty(), Optional.empty()));
    }

    @Test
    public void summarize_valueInNoCategory_countsAsOther() {
        Map<String, Predicate<Integer>> evenOnly = Map.of("even", value -> value % 2 == 0);
        assertEquals("1 even, 1 other, 1 not recorded.\nNot recorded: Ben Lim.",
                WeeklyRecordSummary.summarize(createEntries(Optional.of(2), Optional.empty(), Optional.of(3)),
                        evenOnly));
    }

    @Test
    public void summarize_nullInputs_throwsNullPointerException() {
        List<WeeklyRecordEntry<Integer>> entries = createEntries(Optional.of(2), Optional.of(3), Optional.empty());
        assertThrows(NullPointerException.class, () -> WeeklyRecordSummary.summarize(null, PARITY_CATEGORIES));
        assertThrows(NullPointerException.class, () -> WeeklyRecordSummary.summarize(entries, null));
    }

    private static String summarize(Optional<Integer> aliceRecord, Optional<Integer> benRecord,
            Optional<Integer> carlRecord) {
        return WeeklyRecordSummary.summarize(createEntries(aliceRecord, benRecord, carlRecord), PARITY_CATEGORIES);
    }

    private static List<WeeklyRecordEntry<Integer>> createEntries(Optional<Integer> aliceRecord,
            Optional<Integer> benRecord, Optional<Integer> carlRecord) {
        return List.of(new WeeklyRecordEntry<>(ALICE, aliceRecord), new WeeklyRecordEntry<>(BEN, benRecord),
                new WeeklyRecordEntry<>(CARL, carlRecord));
    }

    private static Map<String, Predicate<Integer>> createParityCategories() {
        Map<String, Predicate<Integer>> categories = new LinkedHashMap<>();
        categories.put("even", value -> value % 2 == 0);
        categories.put("odd", value -> value % 2 != 0);
        return categories;
    }
}
