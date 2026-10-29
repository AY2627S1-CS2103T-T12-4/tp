package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import seedu.address.model.student.WeeklyRecordEntry;

/**
 * Builds the summary that follows the header of a weekly attendance or participation listing, so that a TA sees the
 * counts and the students without a record without scrolling through every row.
 */
final class WeeklyRecordSummary {
    private static final String LABEL_OTHER = "other";
    private static final String LABEL_NOT_RECORDED = "not recorded";
    private static final String MESSAGE_NOT_RECORDED_NAMES = "Not recorded: %s.";

    private WeeklyRecordSummary() {
    }

    /**
     * Returns a line of counts such as {@code 18 present, 1 absent, 1 not recorded.}, followed by a line naming the
     * students without a record when there are any.
     * Each category in {@code categories} is counted under its label, in the map's iteration order. Recorded values
     * that match no category are counted as {@code other}. Zero counts are left out, so the line only names what
     * happened in the week; since {@code entries} is not empty, at least one count is shown.
     *
     * @param entries The listing's entries, one per student, in roster order. Must not be empty.
     * @param categories Labels mapped to the disjoint conditions that recorded values are counted by.
     */
    static <T> String summarize(List<WeeklyRecordEntry<T>> entries, Map<String, Predicate<T>> categories) {
        requireAllNonNull(entries, categories);
        assert !entries.isEmpty() : "A weekly summary needs at least one student";

        List<String> counts = new ArrayList<>();
        // Starts at all recorded values; each category subtracts its matches, leaving those no category matches.
        long uncategorizedCount = countRecords(entries, value -> true);
        for (Map.Entry<String, Predicate<T>> category : categories.entrySet()) {
            long count = countRecords(entries, category.getValue());
            uncategorizedCount -= count;
            addNonZeroCount(counts, count, category.getKey());
        }
        addNonZeroCount(counts, uncategorizedCount, LABEL_OTHER);

        List<String> unrecordedNames = getUnrecordedNames(entries);
        addNonZeroCount(counts, unrecordedNames.size(), LABEL_NOT_RECORDED);
        String countLine = String.join(", ", counts) + ".";
        if (unrecordedNames.isEmpty()) {
            return countLine;
        }
        return countLine + "\n" + String.format(MESSAGE_NOT_RECORDED_NAMES, String.join(", ", unrecordedNames));
    }

    private static <T> long countRecords(List<WeeklyRecordEntry<T>> entries, Predicate<T> isCounted) {
        return entries.stream()
                .map(WeeklyRecordEntry::getRecord)
                .flatMap(Optional::stream)
                .filter(isCounted)
                .count();
    }

    private static <T> List<String> getUnrecordedNames(List<WeeklyRecordEntry<T>> entries) {
        return entries.stream()
                .filter(entry -> entry.getRecord().isEmpty())
                .map(entry -> entry.getStudent().getName().toString())
                .collect(Collectors.toList());
    }

    private static void addNonZeroCount(List<String> counts, long count, String label) {
        if (count > 0) {
            counts.add(count + " " + label);
        }
    }
}
