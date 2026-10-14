package seedu.address.model.student;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** Represents one student's record, or the absence of a record, in a group-wide weekly listing. */
public final class WeeklyRecordEntry<T> {
    private static final String MESSAGE_NOT_RECORDED = "Not recorded";

    private final Student student;
    private final Optional<T> record;

    /** Creates a listing entry for {@code student} and their optional weekly record. */
    public WeeklyRecordEntry(Student student, Optional<T> record) {
        this.student = requireNonNull(student);
        this.record = requireNonNull(record);
    }

    public Student getStudent() {
        return student;
    }

    public Optional<T> getRecord() {
        return record;
    }

    /** Formats the value, or returns {@code Not recorded} when this student has no value for the week. */
    public String formatValueOrNotRecorded(Function<? super T, String> formatter) {
        requireNonNull(formatter);
        return record.map(value -> requireNonNull(formatter.apply(value))).orElse(MESSAGE_NOT_RECORDED);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof WeeklyRecordEntry<?> otherEntry)) {
            return false;
        }
        return student.equals(otherEntry.student) && record.equals(otherEntry.record);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, record);
    }
}
